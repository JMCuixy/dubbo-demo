package com.demo.sequence.util;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class Constant {

    /**
     * 唯一索引冲突的sqlState
     */
    public static String DuplicateKeySqlState = "23000";

    /**
     * 数据库连接异常sqlState前缀
     */
    public static String DataAccessSqlStatePrefix = "08";

    /**
     * capacity到达多少的时候可以开启异步加载机制
     */
    public static int capacityLimit = 1000000;

    /**
     * 号池中剩余多少开始load数据
     */
    public static double surplusOfBuffer = 0.8;
}
