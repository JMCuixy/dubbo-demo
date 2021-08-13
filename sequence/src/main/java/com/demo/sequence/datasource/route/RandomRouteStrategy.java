package com.demo.sequence.datasource.route;

import com.demo.sequence.datasource.info.DataSourceInfo;

import java.util.Collection;
import java.util.Random;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class RandomRouteStrategy implements RouteStrategy {
    @Override
    public int sharding(int shardingDBCount, Collection<DataSourceInfo> dataSourcePool) {

        return new Random().nextInt(shardingDBCount);
    }
}
