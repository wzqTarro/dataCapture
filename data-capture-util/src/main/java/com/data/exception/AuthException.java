package com.data.exception;

/**
 * 认证异常类
 * @author Alex
 *
 */
public class AuthException extends RuntimeException {

	private static final long serialVersionUID = 3094058061698226315L;

	public AuthException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AuthException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AuthException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
