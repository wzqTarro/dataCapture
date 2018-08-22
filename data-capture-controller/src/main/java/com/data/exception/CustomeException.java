package com.data.exception;

/**
 * 自定义异常类
 * @author alex
 *
 */
public class CustomeException extends RuntimeException {

	private static final long serialVersionUID = -5188801410184203458L;

	public CustomeException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CustomeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public CustomeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CustomeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CustomeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
