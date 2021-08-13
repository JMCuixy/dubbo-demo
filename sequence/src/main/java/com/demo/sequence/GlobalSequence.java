package com.demo.sequence;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public interface GlobalSequence {

	/**
	 * 获取一个全局唯一自增号
	 *
	 * @param appName 业务编码
	 * @return
	 */
	public long nextValue(String appName); 
}
