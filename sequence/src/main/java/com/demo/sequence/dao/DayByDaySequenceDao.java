package com.demo.sequence.dao;

import com.demo.sequence.domain.SequenceByDay;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public interface DayByDaySequenceDao {
	
	int save(SequenceByDay sequence);

	/**
	 * 获取一个全局唯一自增号
	 * 
	 * @param appName
	 *            业务编码
	 * @return
	 * @author: yanhuajian 2016年3月18日下午8:38:00
	 */
	SequenceByDay nextValue(String appName, String currentDay);

	/**
	 * 根据业务编码进行更新
     * @param appName 业务编码
     * @param currentDay 当前日期
     * @param oldValue 旧值，数据库做乐观锁用
     * @param offset 要增加的偏移量
	 */
	int updateValueByAppName(String appName, String currentDay, Long oldValue, Integer offset);
}
