package com.demo.sequence.util;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class ShardingOffsetUtil {

    /**
     * 计算下一个偏移量
     *
     * @return
     */
    public static int nextOffset(int shardingDBCount, int capacity) {
        return shardingDBCount * capacity;
    }

    /**
     * 计算sequence的下一个号段的结束值
     *
     * @return
     */
    public static long nextValue(long oldValue, int shardingDBCount, int capacity) {
        return oldValue + shardingDBCount * capacity;
    }

    /**
     * 根据对当前value对数据库数取模，判断是否符合当前的db
     *
     * @param value
     * @param shardingDBCount
     * @param dataBaseNo
     * @return
     */
    public static boolean holding(long value, int shardingDBCount, int dataBaseNo) {
        return value % shardingDBCount == dataBaseNo;
    }

}
