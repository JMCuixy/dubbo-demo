package com.demo.sequence.dao.impl;

import com.demo.sequence.dao.GlobalSequenceDao;
import com.demo.sequence.datasource.DataSourceHolder;
import com.demo.sequence.datasource.DataSourceRouter;
import com.demo.sequence.domain.Sequence;
import com.demo.sequence.schema.GlobalSequenceTable;
import com.demo.sequence.util.JdbcHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DefaultGlobalSequenceDao implements GlobalSequenceDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGlobalSequenceDao.class);

	/**
	 * 数据库表结构
	 */
	private GlobalSequenceTable sequenceTable = new GlobalSequenceTable();

	@Override
	public Sequence nextValue(String appName) {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT ").append(this.sequenceTable.getAppValueName()).append(" FROM ").append(this.sequenceTable.getTableName())
				.append(" WHERE ").append(this.sequenceTable.getAppKeyName()).append(" = ?");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("nextValue--->{}", selectSql.toString());
        }

		Sequence sequence = null;

        DataSourceRouter.DataSourcePair dataSourcePair = DataSourceHolder.getDataSource();
		JdbcHolder jdbcHolder = new JdbcHolder(dataSourcePair.getDataSource());
		List<Map<String, Object>> list = jdbcHolder.executeQuery(selectSql.toString(), new String[] { appName });
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			if (map.get(this.sequenceTable.getAppValueName()) instanceof Long) {
				sequence = new Sequence(appName, (Long) map.get(this.sequenceTable.getAppValueName()));
			}
		}

		return sequence;
	}

	@Override
	public int updateValueByAppName(String appName, Long oldValue, Integer offset) {

		StringBuilder updateSql = new StringBuilder();

		//update {tableName} set {appValueName} = {appValueName} + ?, {updateTimeName} = now() where {appKeyName} = ? and {appValueName} = ?
		updateSql.append("UPDATE ").append(this.sequenceTable.getTableName())
                .append(" SET ").append(this.sequenceTable.getAppValueName()).append(" = ").append(this.sequenceTable.getAppValueName()).append(" + ?, ")
                .append(this.sequenceTable.getUpdateTimeName()).append(" = now() WHERE ")
				.append(this.sequenceTable.getAppKeyName()).append(" = ? AND ")
                .append(this.sequenceTable.getAppValueName()).append(" = ?");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateValueByAppName--->{}", updateSql.toString());
        }

        DataSourceRouter.DataSourcePair dataSourcePair = DataSourceHolder.getDataSource();
        JdbcHolder jdbcHolder = new JdbcHolder(dataSourcePair.getDataSource());
		int ret = jdbcHolder.executeUpdate(updateSql.toString(), new Object[] { offset, appName, oldValue });

		return ret;
	}

    public void setSequenceTable(GlobalSequenceTable sequenceTable) {
		this.sequenceTable = sequenceTable;
	}

}
