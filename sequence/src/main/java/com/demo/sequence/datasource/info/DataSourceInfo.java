package com.demo.sequence.datasource.info;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DataSourceInfo {
    private int dataSourceNo;
    private boolean status;
    private DataSource dataSource;

    public DataSourceInfo(int dataSourceNo, boolean status, DataSource dataSource) {
        this.dataSourceNo = dataSourceNo;
        this.status = status;
        this.dataSource = dataSource;
    }

    public int getDataSourceNo() {
        return dataSourceNo;
    }

    public void setDataSourceNo(int dataSourceNo) {
        this.dataSourceNo = dataSourceNo;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSourceInfo that = (DataSourceInfo) o;
        return dataSourceNo == that.dataSourceNo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSourceNo);
    }
}
