package com.demo.sequence.datasource.route;

import com.demo.sequence.datasource.info.DataSourceInfo;

import java.util.Collection;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public interface RouteStrategy {

    int sharding(int shardingDBCount, Collection<DataSourceInfo> dataSourcePool);

}
