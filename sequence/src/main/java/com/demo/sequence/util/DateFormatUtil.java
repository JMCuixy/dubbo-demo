package com.demo.sequence.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class DateFormatUtil {

	private String pattern;

	public DateFormatUtil(String pattern) {
		this.pattern = pattern;
	}

	private final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(pattern);
		}
	};

	public final String format(Date date) {
		return df.get().format(date);
	}

	public Date parse(String source) throws ParseException {
		return df.get().parse(source);
	}
}