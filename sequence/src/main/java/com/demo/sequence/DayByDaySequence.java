package com.demo.sequence;

import java.util.Date;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public interface DayByDaySequence {

	/**
	 * 获取一个按天唯一自增号
	 * 需要传入一个当前日期给业务端自己控制当前天，防止跨零点时间不一致时，出现号冲突
	 *
	 * @param appName 业务编码
	 * @param currentDay 当前日期
	 * @return 
	 */
	public long nextValue(String appName, Date currentDay);

}
