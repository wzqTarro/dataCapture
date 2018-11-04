package com.data.exception;

/**
 * 全局异常类
 * @author Alex
 *
 */
public class GlobalException extends RuntimeException {

	private static final long serialVersionUID = -6522361417156895776L;
	
	private String errorCode;
	
	private String errorMsg;
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public GlobalException() {}
	
	public GlobalException(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

}
