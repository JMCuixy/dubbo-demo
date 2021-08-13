package com.demo.sequence.util;

import com.demo.sequence.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class JdbcHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcHolder.class);

    /**
     * 数据库连接池
     */
    private DataSource dataSource;

    /**
     * 创建数据库连接对象
     */
    private Connection connection = null;

    /**
     * 创建PreparedStatement对象
     */
    private PreparedStatement preparedStatement = null;

    /**
     * 创建结果集对象
     */
    private ResultSet resultSet = null;

    public JdbcHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 初始化
     */
    private Connection getConnection() {
        try {
            // 从连接池拿一个连接
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApplicationException(e);
        }
    }

    /**
     * insert update delete SQL语句的执行的统一方法
     *
     * @param sql    SQL语句
     * @param params 参数数组，若没有参数则为null
     * @return 受影响的行数
     */
    public int executeUpdate(String sql, Object[] params) {
        // 受影响的行数
        int affectedLine = 0;

        try {
            // 获取连接
            this.connection = this.getConnection();
            // 调用SQL
            preparedStatement = connection.prepareStatement(sql);

            // 参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            // 执行
            affectedLine = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApplicationException(e);
        } finally {
            // 释放资源
            closeAll();
        }
        return affectedLine;
    }

    /**
     * SQL 查询将查询结果直接放入ResultSet中
     *
     * @param sql    SQL语句
     * @param params 参数数组，若没有参数则为null
     * @return 结果集
     */
    private ResultSet executeQueryRS(String sql, Object[] params) {
        try {
            // 获取连接
            this.connection = this.getConnection();
            // 调用SQL
            preparedStatement = connection.prepareStatement(sql);

            // 参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }

            // 执行
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApplicationException(e);
        }

        return resultSet;
    }

    /**
     * 获取结果集，并将结果放在List中
     *
     * @param sql SQL语句
     * @return List 结果集
     */
    public List<Map<String, Object>> executeQuery(String sql, Object[] params) {
        // 执行SQL获得结果集
        ResultSet rs = executeQueryRS(sql, params);

        // 创建ResultSetMetaData对象
        ResultSetMetaData rsmd = null;

        // 结果集列数
        int columnCount = 0;
        try {
            rsmd = rs.getMetaData();

            // 获得结果集列数
            columnCount = rsmd.getColumnCount();
        } catch (SQLException e1) {
            LOGGER.error(e1.getMessage(), e1);
            throw new ApplicationException(e1);
        }

        // 创建List
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        try {
            // 将ResultSet的结果保存到List中
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(rsmd.getColumnLabel(i), rs.getObject(i));
                }
                list.add(map);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApplicationException(e);
        } finally {
            // 关闭所有资源
            closeAll();
        }

        return list;
    }

    /**
     * 关闭所有资源
     */
    private void closeAll() {
        // 关闭结果集对象
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApplicationException(e);
        } finally {
            // 关闭PreparedStatement对象
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
                throw new ApplicationException(e);
            } finally {
                // 将Connection归还连接池
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new ApplicationException(e);
                }
            }
        }

    }
}
