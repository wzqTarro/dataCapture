package com.data.constant.enums;

/**
 * 公共代码编码枚举类
 * @author Alex
 *
 */
public enum CodeEnum {

	/**成功**/
	RESPONSE_00_CODE("00"),
	
	/**失败**/
	RESPONSE_99_CODE("99")
	;
	
	String value;
	
	public String getValue() {
		return value;
	}
	
	private CodeEnum(String value) {
		this.value = value; 
	}
	
}
