package com.demo.sequence.exception;

/**
 * @Author: xiuyin.cui@abite.com
 * @Date: 2018/11/05
 */

public class SequenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SequenceException() {
		super();
	}

	public SequenceException(String message) {
		super(message);
	}

	public SequenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SequenceException(Throwable cause) {
		super(cause);
	}
}
