package com.demo.sequence.impl;

import com.demo.sequence.DayByDaySequence;
import com.demo.sequence.SequenceBufferPool;
import com.demo.sequence.dao.DayByDaySequenceDao;
import com.demo.sequence.datasource.DataSourceHolder;
import com.demo.sequence.datasource.DataSourceManagerFactory;
import com.demo.sequence.datasource.DataSourceRouter;
import com.demo.sequence.datasource.info.DataSourceInfo;
import com.demo.sequence.domain.SequenceByDay;
import com.demo.sequence.exception.ApplicationException;
import com.demo.sequence.exception.SequenceException;
import com.demo.sequence.task.DataSourcePoolExecutor;
import com.demo.sequence.task.DayByDaySequenceBufferPoolCheckTask;
import com.demo.sequence.task.SequenceBufferPoolExecutor;
import com.demo.sequence.util.Constant;
import com.demo.sequence.util.DateFormatUtil;
import com.demo.sequence.util.ShardingOffsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DefaultDayByDaySequence implements DayByDaySequence {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDayByDaySequence.class);

    /**
     * capacity指的是每次从数据库申请出来的的订单id个数，这里默认为500
     */
    protected int capacity = 500;

    /**
     * 日期格式规则
     */
    private DateFormatUtil dateFormat = new DateFormatUtil("yyyyMMdd");

    /**
     * 号池
     */
    protected static SequenceBufferPool<String, SequenceByDay> BUFFER_POOL = new SequenceBufferPool<String, SequenceByDay>();

    /**
     * 程序并发锁，每个数据库链接一把锁，防止数据库层面并发，多个数据库则可以同时访问
     */
    private Map<Integer, Lock> locks;

    private DataSourceManagerFactory dataSourceManagerFactory;

    private DayByDaySequenceDao sequenceDao;

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

    /**
     * 从队列中弹出一个订单号对象。<br>
     * 这里必须保证队列中取出来的日期和当前日期一致，否则如果取到之前日期的值，则可能会造成订单号冲突
     */
    protected SequenceByDay pollOrderFromQueue(String appName, String currentDayFormat) {

        SequenceByDay sequence = null;
        if ((sequence = BUFFER_POOL.poll(appName)) != null) {
            // 如果队列中取出来的日期和当前日期不一致，说明队列中有昨日的数据没有消费完
            if (!sequence.getCreateDay().equals(currentDayFormat)) {
                // 清空队列，incrementId置为null
                BUFFER_POOL.clear(appName);

                return sequence = null;
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("队列中还有id，直接返回value，当前的value={}，appName={}", sequence.getAppValue(), appName);

            return sequence;
        }
        return sequence;
    }

    @Override
    public long nextValue(String appName, Date currentDay) {
        String currentDayFormat = this.dateFormat.format(currentDay);

        SequenceByDay sequence = null;
        while (sequence == null) {
            if ((sequence = this.pollOrderFromQueue(appName, currentDayFormat)) != null) {
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
                if ((sequence = this.pollOrderFromQueue(appName, currentDayFormat)) != null) {

                    // 如果号池里面的数量少于步长的1/2，则异步进行一次reload填充号池，减少每次号池空了后查询数据库的性能损耗
                    // 放在这里load是因为要利用外面的lock
                    if (capacity >= Constant.capacityLimit && BUFFER_POOL.size(appName) < capacity * Constant.surplusOfBuffer) {
                        SequenceBufferPoolExecutor.execute(new DayByDaySequenceBufferPoolCheckTask(this, appName, currentDay));
                    }
                    break;
                }

                this.loadValue(appName, currentDayFormat);

            } catch (SequenceException e) {
                LOGGER.error("出现异常，appName={}, error={}", appName, e);
                continue;
            } finally {
                lock.unlock();
            }
        }
        return sequence.getAppValue();
    }

    /**
     * 从数据库加载数据到buffer中
     *
     * @param appName
     * @param currentDayFormat
     */
    private void loadValue(String appName, String currentDayFormat) {

        DataSourceRouter.DataSourcePair dataSourcePair = DataSourceHolder.getDataSource();

        // 获取出数据库的value值（oldValue）
        SequenceByDay oldSequence = null;
        try {
            oldSequence = this.getOldValue(appName, currentDayFormat);
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
        Long oldValue = oldSequence.getAppValue();

        // 下一个号段起始值
        long nextValue = ShardingOffsetUtil.nextValue(oldValue, dataSourceManagerFactory.getDataSourceCount(), capacity);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("DB【{}】开始将数据加入到队列中", DataSourceHolder.getDataSource().getDataSourceNo());
        }
        // 将oldValue到oldValue+capacity这capacity个数值加入到队列中，
        for (long seq = oldValue; seq < nextValue; seq++) {
            if (ShardingOffsetUtil.holding(seq, dataSourceManagerFactory.getDataSourceCount(), dataSourcePair.getDataSourceNo())) {
                BUFFER_POOL.offer(appName, new SequenceByDay(appName, seq, currentDayFormat));
            }
        }
    }

    /**
     * 获取出数据库的value值（oldValue）<br>
     * 如果数据库保存或更新失败，则抛出SequenceException异常，返回给上层处理
     */
    protected SequenceByDay getOldValue(String appName, String currentDay) {
        Long oldValue = 1L;
        SequenceByDay oldSequence = new SequenceByDay(appName, oldValue, currentDay);

        // 根据bussCode和当前日期查出数据库里面当前的value值(oldValue)
        SequenceByDay sequence = this.sequenceDao.nextValue(appName, currentDay);
        // order为null，说明是第一次查询。如果两个线程同时插入操作，则由于唯一约束，第二个线程执行会抛出异常
        if (sequence == null) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("数据库中没有记录，appName={}", appName);
            // 第一次插入一定要this.nextValue(oldValue)，将初始值设置为步长
            int offset = ShardingOffsetUtil.nextOffset(dataSourceManagerFactory.getDataSourceCount(), capacity);
            sequence = new SequenceByDay(appName, Long.valueOf(offset), currentDay);
            try {
                int saveRet = this.sequenceDao.save(sequence);
                if (saveRet == 0) {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("save失败，appName={}, currentDay={}", appName, currentDay);
                    throw new SequenceException("save失败");
                }
            } catch (ApplicationException e) {
                Throwable t = null;
                if ((t = e.getCause()) != null) {
                    if (t instanceof SQLException) {
                        String sqlState = ((SQLException) t).getSQLState();
                        // 唯一约束冲突
                        if (Constant.DuplicateKeySqlState.equals(sqlState)) {
                            throw new SequenceException(e);
                        }
                    }
                }
                throw e;
            }
        } else {
            oldValue = sequence.getAppValue();
            oldSequence = new SequenceByDay(appName, oldValue + 1, currentDay);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("队列中id已经消费完了，开始重新查询数据库，当前的value={}，appName={}", oldValue, appName);
            }
            // 将数据库中该bussCode和当前日期对应的value更新成为oldValue+capacity，
            int offset = ShardingOffsetUtil.nextOffset(dataSourceManagerFactory.getDataSourceCount(), capacity);
            int updateRet = this.sequenceDao.updateValueByAppName(appName, currentDay, oldValue, offset);
            // 如果没有update成功，则说明集群环境下其他实例已经update过了，这里需要重新进入
            if (updateRet == 0) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("updateValueByAppName失败，appName={}", appName);
                throw new SequenceException("updateValueByAppName失败");
            }
        }
        return oldSequence;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setSequenceDao(DayByDaySequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }

    public void setDataSourceManagerFactory(DataSourceManagerFactory dataSourceManagerFactory) {
        this.dataSourceManagerFactory = dataSourceManagerFactory;
    }

}
