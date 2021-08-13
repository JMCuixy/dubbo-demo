package com.demo.sequence.dao.impl;

import com.demo.sequence.dao.DayByDaySequenceDao;
import com.demo.sequence.datasource.DataSourceHolder;
import com.demo.sequence.datasource.DataSourceRouter;
import com.demo.sequence.domain.SequenceByDay;
import com.demo.sequence.schema.DayByDaySequenceTable;
import com.demo.sequence.util.JdbcHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DefaultDayByDaySequenceDao implements DayByDaySequenceDao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDayByDaySequenceDao.class);

    /**
     * 数据源
     */
    private List<DataSource> dataSourceList;

	/**
	 * 每日序列数据库表配置
	 */
	private DayByDaySequenceTable sequenceTable = new DayByDaySequenceTable();

	@Override
	public int save(SequenceByDay sequence) {
		StringBuilder saveSql = new StringBuilder();
		saveSql.append("INSERT INTO ").append(this.sequenceTable.getTableName()).append(" (").append(this.sequenceTable.getAppKeyName())
				.append(", ").append(this.sequenceTable.getAppValueName()).append(", ").append(this.sequenceTable.getCreateDayName())
				.append(", ").append(this.sequenceTable.getCreateTimeName()).append(", ").append(this.sequenceTable.getUpdateTimeName())
				.append(")").append(" VALUES (?, ?, ?, now(), now())");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("save--->{}", saveSql.toString());
        }

        DataSourceRouter.DataSourcePair dataSourcePair = DataSourceHolder.getDataSource();
        JdbcHolder jdbcHolder = new JdbcHolder(dataSourcePair.getDataSource());
		int ret = jdbcHolder.executeUpdate(saveSql.toString(),
				new Object[] { sequence.getAppName(), sequence.getAppValue(), sequence.getCreateDay() });

		return ret;
	}

	@Override
	public SequenceByDay nextValue(String appName, String currentDay) {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT ").append(this.sequenceTable.getAppValueName()).append(" FROM ").append(this.sequenceTable.getTableName())
				.append(" WHERE ").append(this.sequenceTable.getAppKeyName()).append(" = ? AND ")
				.append(this.sequenceTable.getCreateDayName()).append(" = ?");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("nextValue--->{}", selectSql.toString());
        }

		SequenceByDay sequence = null;

        DataSourceRouter.DataSourcePair dataSourcePair = DataSourceHolder.getDataSource();
        JdbcHolder jdbcHolder = new JdbcHolder(dataSourcePair.getDataSource());
		List<Map<String, Object>> list = jdbcHolder.executeQuery(selectSql.toString(), new String[] { appName, currentDay });
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			if (map.get(this.sequenceTable.getAppValueName()) instanceof Long) {
				sequence = new SequenceByDay(this.sequenceTable.getAppValueName(), (Long) map.get(this.sequenceTable.getAppValueName()), currentDay);
			}
		}

		return sequence;
	}

	@Override
	public int updateValueByAppName(String appName, String currentDay, Long oldValue, Integer offset) {

		StringBuilder updateSql = new StringBuilder();

        //update {tableName} set {appValueName} = {appValueName} + ?, {updateTimeName} = now() where {appKeyName} = ? and {createDayName} = ? and {appValueName} = ?
		updateSql.append("UPDATE ").append(this.sequenceTable.getTableName())
                .append(" SET ").append(this.sequenceTable.getAppValueName()).append(" = ").append(this.sequenceTable.getAppValueName()).append(" + ?, ")
                .append(this.sequenceTable.getUpdateTimeName()).append(" = now() WHERE ")
				.append(this.sequenceTable.getAppKeyName()).append(" = ? AND ")
                .append(this.sequenceTable.getCreateDayName()).append(" = ? AND ")
				.append(this.sequenceTable.getAppValueName()).append(" = ?");

		if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateValueByAppName--->{}", updateSql.toString());
        }

        DataSourceRouter.DataSourcePair dataSourcePair = DataSourceHolder.getDataSource();
        JdbcHolder jdbcHolder = new JdbcHolder(dataSourcePair.getDataSource());
		int ret = jdbcHolder.executeUpdate(updateSql.toString(), new Object[] { offset, appName, currentDay, oldValue });

		return ret;
	}

    public void setDataSourceList(List<DataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }

    public void setSequenceTable(DayByDaySequenceTable sequenceTable) {
		this.sequenceTable = sequenceTable;
	}

}
