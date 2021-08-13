package com.demo.sequence.impl;

import com.demo.sequence.GlobalSequence;
import com.demo.sequence.SequenceBufferPool;
import com.demo.sequence.dao.GlobalSequenceDao;
import com.demo.sequence.datasource.DataSourceHolder;
import com.demo.sequence.datasource.DataSourceManagerFactory;
import com.demo.sequence.datasource.DataSourceRouter;
import com.demo.sequence.datasource.info.DataSourceInfo;
import com.demo.sequence.domain.Sequence;
import com.demo.sequence.exception.ApplicationException;
import com.demo.sequence.exception.SequenceException;
import com.demo.sequence.task.DataSourcePoolExecutor;
import com.demo.sequence.task.GlobalSequenceBufferPoolCheckTask;
import com.demo.sequence.task.SequenceBufferPoolExecutor;
import com.demo.sequence.util.Constant;
import com.demo.sequence.util.ShardingOffsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DefaultGlobalSequence implements GlobalSequence {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGlobalSequence.class);

    /**
     * capacity 指的是每次从数据库申请出来的的订单id个数，这里默认为 500
     */
    protected int capacity = 500;
    /**
     * 号池
     */
    protected static SequenceBufferPool<String, Long> BUFFER_POOL = new SequenceBufferPool<>();

    /**
     * 程序并发锁，每个数据库链接一把锁，防止数据库层面并发，多个数据库则可以同时访问
     */
    private Map<Integer, Lock> locks;

    private DataSourceManagerFactory dataSourceManagerFactory;

    protected GlobalSequenceDao sequenceDao;

    /**
     * 初始化锁对象
     */
    public void init() {
        int dataSourceCount = dataSourceManagerFactory.getDataSourceCount();
        locks = new ConcurrentHashMap<>(dataSourceCount);

        for (Map.Entry<Integer, DataSourceInfo> entry : dataSourceManagerFactory.getDataSourcePool().entrySet()) {
            locks.put(entry.getValue().getDataSourceNo(), new ReentrantLock());
        }
    }

    @Override
    public long nextValue(String appName) {
        Long incrementId = null;
        while (true) {
            if ((incrementId = this.pollOrderFromQueue(appName)) != null) {
                break;
            }

            // 选择数据源
            DataSourceRouter.DataSourcePair dataSourcePair = DataSourceRouter.dataSourceSelect(dataSourceManagerFactory);
            // 绑定数据源
            DataSourceHolder.bindDataSource(dataSourcePair);

            // 这里加锁只能控制本实例层面不会出现并发，多个实例并发的情况则靠数据库乐观锁来控制
            Lock lock = locks.get(dataSourcePair.getDataSourceNo());
            lock.lock();
            try {
                // 这里需要再次查询一下队列是因为，如果第一次过来队列中没值，那么所有请求线程将会被lock锁在上面的lock处，
                // 而接下来的所有线程会一个个的进入数据库查询，这里做法是当第一个线程执行完入队操作后，队里里面已经有值了，其他线程只要从队列取值就可以了。
                if ((incrementId = this.pollOrderFromQueue(appName)) != null) {

                    // 如果号池里面的数量少于步长的1/2，则异步进行一次reload填充号池，减少每次号池空了后查询数据库的性能损耗
                    // 放在这里load是因为要利用外面的lock
                    if (capacity >= Constant.capacityLimit && BUFFER_POOL.size(appName) < capacity * Constant.surplusOfBuffer) {
                        SequenceBufferPoolExecutor.execute(new GlobalSequenceBufferPoolCheckTask(this, appName));
                    }
                    break;
                }
                this.loadValue(appName);
            } catch (SequenceException e) {
                LOGGER.error("出现异常，appName={}, error={}", appName, e);
                continue;
            } finally {
                lock.unlock();
            }

            // 解除数据源
            DataSourceHolder.unbindDataSource();
        }
        return incrementId;
    }

    /**
     * 从队列中弹出一个订单号对象。<br>
     */
    protected Long pollOrderFromQueue(final String appName) {
        Long incrementId = null;
        if ((incrementId = BUFFER_POOL.poll(appName)) != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("队列中还有id，直接返回value，当前的value={}，appName={}", incrementId, appName);
            }
        }
        return incrementId;
    }

    /**
     * 获取出数据库的value值（oldValue）<br>
     * 如果数据库保存或更新失败，则抛出 SequenceException 异常，返回给上层处理
     */
    protected Long getOldValue(String appName) {
        Long oldValue = 1L;
        // 根据appName和当前日期查出数据库里面当前的value值(oldValue)
        Sequence sequence = this.sequenceDao.nextValue(appName);
        // sequence为null，说明当前还没有申请该appName，需要进行申请。
        if (sequence == null) {
            LOGGER.info("数据库中没有记录，appName={}", appName);
            throw new IllegalArgumentException("数据库中没有记录appName=" + appName);
        } else {
            oldValue = sequence.getAppValue();
            LOGGER.info("队列中id已经消费完了，开始重新查询数据库，当前的value={}，appName={}", sequence.getAppValue(), appName);
            // 将数据库中该appName和当前日期对应的value更新成为oldValue+capacity，
            int offset = ShardingOffsetUtil.nextOffset(dataSourceManagerFactory.getDataSourceCount(), capacity);
            int updateRet = this.sequenceDao.updateValueByAppName(appName, oldValue, offset);
            // 如果没有update成功，则说明集群环境下其他实例已经update过了，这里需要重新进入
            if (updateRet == 0) {
                LOGGER.info("updateValueByAppName失败，appName={}", appName);
                throw new SequenceException("updateValueByAppName失败");
            }
        }

        return oldValue;
    }

    /**
     * 从数据库加载数据到buffer中
     *
     * @param appName
     */
    public void loadValue(String appName) {

        DataSourceRouter.DataSourcePair dataSourcePair = DataSourceHolder.getDataSource();

        // 获取出数据库的value值（oldValue）
        Long oldValue = null;
        try {
            oldValue = this.getOldValue(appName);
        } catch (ApplicationException e) {
            Throwable t = null;
            if ((t = e.getCause()) != null && t instanceof SQLException) {
                String sqlState = ((SQLException) t).getSQLState();
                // 数据库连接异常
                if (sqlState != null && sqlState.startsWith(Constant.DataAccessSqlStatePrefix)) {
                    LOGGER.error("数据库【{}】连接异常,重新选一个数据库进行连接", dataSourcePair.getDataSourceNo());
                    // 重新选一个数据库进行连接
                    dataSourceManagerFactory.invalidDataSource(dataSourcePair.getDataSourceNo());
                    DataSourcePoolExecutor.execute(dataSourceManagerFactory);
                    throw new SequenceException("数据库【" + dataSourcePair.getDataSourceNo() + "】连接异常,重新选一个数据库进行连接");
                }
            }
            throw e;
        }

        // 下一个号段起始值
        long nextValue = ShardingOffsetUtil.nextValue(oldValue, dataSourceManagerFactory.getDataSourceCount(), capacity);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("DB【{}】开始将数据加入到队列中", DataSourceHolder.getDataSource().getDataSourceNo());
        }
        // 将oldValue到oldValue+capacity这capacity个数值加入到队列中，
        for (long seq = oldValue; seq < nextValue; seq++) {
            if (ShardingOffsetUtil.holding(seq, dataSourceManagerFactory.getDataSourceCount(), dataSourcePair.getDataSourceNo())) {
                BUFFER_POOL.offer(appName, seq);
            }
        }

    }


    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setSequenceDao(GlobalSequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }

    public void setDataSourceManagerFactory(DataSourceManagerFactory dataSourceManagerFactory) {
        this.dataSourceManagerFactory = dataSourceManagerFactory;
    }

    public DataSourceManagerFactory getDataSourceManagerFactory() {
        return dataSourceManagerFactory;
    }
}
