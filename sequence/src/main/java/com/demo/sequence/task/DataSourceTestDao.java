package com.demo.sequence.task;

import com.demo.sequence.util.JdbcHolder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DataSourceTestDao {

    private DataSource dataSource;

    public DataSourceTestDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean test() {
        try {
            String testSql = "SELECT 1";
            JdbcHolder jdbcHolder = new JdbcHolder(dataSource);

            List<Map<String, Object>> list = jdbcHolder.executeQuery(testSql, null);
            if (list != null && list.size() > 0) {
                Map<String, Object> map = list.get(0);
                if (map.get("1") != null && (Long) map.get("1") == 1) {
                    return true;
                }
            }
        } catch (Throwable e) {
            return false;
        }

        return false;
    }
}
