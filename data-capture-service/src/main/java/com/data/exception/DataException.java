package com.data.exception;

/**
 * 数据自定义异常
 * @author Tarro
 *
 */
public class DataException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4751684850728689888L;
	
	public DataException(String msg) {
		super(msg);
	}
}
