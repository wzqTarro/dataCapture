package com.data.exception;

/**
 * 数据自定义异常
 * @author Tarro
 *
 */
public class DataException extends RuntimeException {

	private static final long serialVersionUID = -4751684850728689888L;

	public DataException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DataException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public DataException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
