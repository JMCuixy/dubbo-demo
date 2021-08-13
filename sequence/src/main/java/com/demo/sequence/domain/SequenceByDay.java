package com.demo.sequence.domain;


/**
 * 自增序列实体类
 * 
 */
public class SequenceByDay extends Sequence {

	private static final long serialVersionUID = 1L;

	/**
	 * 序列号生成日期
	 */
	private String createDay;

	public SequenceByDay() {
	}

	public SequenceByDay(String appName, Long appValue, String createDay) {
		super(appName, appValue);
		this.createDay = createDay;
	}

	public String getCreateDay() {
		return createDay;
	}

	public void setCreateDay(String createDay) {
		this.createDay = createDay;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
