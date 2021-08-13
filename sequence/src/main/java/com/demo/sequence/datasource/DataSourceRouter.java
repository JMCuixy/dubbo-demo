package com.demo.sequence.datasource;

import com.demo.sequence.datasource.info.DataSourceInfo;
import com.demo.sequence.datasource.route.RandomRouteStrategy;
import com.demo.sequence.datasource.route.RouteStrategy;
import com.demo.sequence.exception.ApplicationException;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DataSourceRouter {

    public static DataSourcePair dataSourceSelect(DataSourceManagerFactory dataSourceManagerFactory) {

        if (dataSourceManagerFactory.isAllInvalid()) {
            throw new ApplicationException("全部数据连接都失效了.");
        }

        RouteStrategy routeStrategy = dataSourceManagerFactory.getRouteStrategy();
        if (routeStrategy == null) {
            routeStrategy = new RandomRouteStrategy();
        }

        int shardingDBCount = dataSourceManagerFactory.getDataSourceCount();
        Collection<DataSourceInfo> dataSourceInfoCollection = dataSourceManagerFactory.getDataSourcePool().values();

        int dataSourceNo = routeStrategy.sharding(shardingDBCount, dataSourceInfoCollection);

        /**
         * 如果选出来的db处于无效状态，则重新进行选举
         */
        while (!dataSourceManagerFactory.getDataSourcePool().get(dataSourceNo).getStatus()) {
            dataSourceNo = routeStrategy.sharding(shardingDBCount, dataSourceInfoCollection);
        }

        DataSource dataSource = dataSourceManagerFactory.getDataSourcePool().get(dataSourceNo).getDataSource();

        return new DataSourcePair(dataSourceNo, dataSource);
    }

    public static class DataSourcePair {
        private int dataSourceNo;
        private DataSource dataSource;

        public DataSourcePair(int dataSourceNo, DataSource dataSource) {
            this.dataSourceNo = dataSourceNo;
            this.dataSource = dataSource;
        }

        public int getDataSourceNo() {
            return dataSourceNo;
        }

        public void setDataSourceNo(int dataSourceNo) {
            this.dataSourceNo = dataSourceNo;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }
    }

}
