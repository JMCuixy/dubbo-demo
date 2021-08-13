package com.demo.sequence.datasource;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DataSourceHolder {

    /**
     * 用于存放当前使用的数据源号
     */
    private static final ThreadLocal<DataSourceRouter.DataSourcePair> CURRENT_DS_HOLDER = new ThreadLocal<>();

    /**
     * 将数据源对象绑定到当前线程
     */
    public static void bindDataSource(DataSourceRouter.DataSourcePair dataSource) {
        CURRENT_DS_HOLDER.set(dataSource);
    }

    /**
     * 从当前线程获得绑定的数据源
     */
    public static DataSourceRouter.DataSourcePair getDataSource() {
        return CURRENT_DS_HOLDER.get();
    }

    /**
     * 解除绑定的数据源
     */
    public static void unbindDataSource() {
        CURRENT_DS_HOLDER.remove();
    }

}
