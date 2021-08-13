package com.demo.sequence.task;

import com.demo.sequence.datasource.DataSourceManagerFactory;
import com.demo.sequence.datasource.info.DataSourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DataSourcePoolExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourcePoolExecutor.class);

    private static ScheduledExecutorService service = null;

    private static boolean isRunning = false;

    private static Lock lock = new ReentrantLock();

    public static void execute(DataSourceManagerFactory dataSourceManagerFactory) {
        lock.lock();
        try {
            if (isRunning || (service != null && !service.isShutdown())) {
                return;
            }
            isRunning = true;
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(new DataSourcePoolTestTask(dataSourceManagerFactory), 1, 10, TimeUnit.SECONDS);
            isRunning = false;
        } finally {
            lock.unlock();
        }
    }

    public static void shutdown() {
        service.shutdown();
    }


    public static class DataSourcePoolTestTask implements Runnable {

        private DataSourceManagerFactory dataSourceManagerFactory;

        public DataSourcePoolTestTask(DataSourceManagerFactory dataSourceManagerFactory) {
            this.dataSourceManagerFactory = dataSourceManagerFactory;
        }

        @Override
        public void run() {
            Map<Integer, DataSourceInfo> dataSourcePool = dataSourceManagerFactory.getDataSourcePool();

            if (dataSourceManagerFactory.isAllValid()) {
                shutdown();
                return;
            }

            for (Map.Entry<Integer, DataSourceInfo> entry : dataSourcePool.entrySet()) {

                DataSourceInfo dataSourceInfo = entry.getValue();

                if (dataSourceInfo.getStatus()) {
                    continue;
                }

                LOGGER.error("开始ping失效的数据源【{}】", dataSourceInfo.getDataSourceNo());

                DataSourceTestDao dataSourceTestDao = new DataSourceTestDao(dataSourceInfo.getDataSource());
                boolean result = dataSourceTestDao.test();

                LOGGER.error("ping失效的数据源【{}】结果是:{}", dataSourceInfo.getDataSourceNo(), result);

                if (result) {
                    LOGGER.error("数据源【{}】恢复，重新激活", dataSourceInfo.getDataSourceNo());
                    // 将数据连接重新激活
                    dataSourceManagerFactory.validDataSource(dataSourceInfo.getDataSourceNo());

                }
            }
        }
    }


}
