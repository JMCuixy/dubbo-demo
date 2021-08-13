package com.demo.sequence.datasource;

import com.demo.sequence.datasource.info.DataSourceInfo;
import com.demo.sequence.datasource.route.RandomRouteStrategy;
import com.demo.sequence.datasource.route.RouteStrategy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DataSourceManagerFactory {

    /**
     * 数据库连接池配置
     */
    private Map<Integer, DataSourceInfo> dataSourcePool;

    /**
     * 数据路由策略
     */
    private RouteStrategy routeStrategy = new RandomRouteStrategy();

    /**
     * 初始化数据库连接池
     *
     * @param dataSourceList
     */
    public void setDataSourceList(List<DataSource> dataSourceList) {
        dataSourcePool = new HashMap<>(8);
        for (int i = 0; i < dataSourceList.size(); i++) {
            dataSourcePool.put(i, new DataSourceInfo(i, true, dataSourceList.get(i)));
        }

    }

    /**
     * 将某个数据库连接设置为无效
     *
     * @param dataSourceNo
     */
    public void invalidDataSource(Integer dataSourceNo) {
        DataSourceInfo deadDataSource = dataSourcePool.get(dataSourceNo);
        // 设为无效
        deadDataSource.setStatus(false);
    }

    /**
     * 将某个数据库连接设置为有效
     *
     * @param dataSourceNo
     */
    public void validDataSource(Integer dataSourceNo) {
        DataSourceInfo deadDataSource = dataSourcePool.get(dataSourceNo);
        // 设为有效
        deadDataSource.setStatus(true);
    }

    /**
     * 查询是否全部数据连接都失效了
     *
     * @return
     */
    public boolean isAllInvalid() {
        for (Map.Entry<Integer, DataSourceInfo> entry : this.dataSourcePool.entrySet()) {
            if (entry.getValue().getStatus()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查询是否全部数据连接都有效
     *
     * @return
     */
    public boolean isAllValid() {
        for (Map.Entry<Integer, DataSourceInfo> entry : this.dataSourcePool.entrySet()) {
            if (!entry.getValue().getStatus()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 总数据库数量
     *
     * @return
     */
    public int getDataSourceCount() {
        return this.getDataSourcePool().size();
    }

    public Map<Integer, DataSourceInfo> getDataSourcePool() {
        return dataSourcePool;
    }

    public RouteStrategy getRouteStrategy() {
        return routeStrategy;
    }

    public void setRouteStrategy(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

}
