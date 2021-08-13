package com.demo.sequence.domain;

import java.io.Serializable;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class Sequence implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 业务名称
	 */
	protected String appName;

	/**
	 * 当前值
	 */
	protected Long appValue;

	public Sequence() {
	}

	public Sequence(String appName, Long appValue) {
		this.appName = appName;
		this.appValue = appValue;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Long getAppValue() {
		return appValue;
	}

	public void setAppValue(Long appValue) {
		this.appValue = appValue;
	}
}
