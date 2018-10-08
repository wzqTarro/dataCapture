package com.data.constant.enums;

/**
 * 公共代码编码枚举类
 * @author Alex
 *
 */
public enum CodeEnum {

	/**成功**/
	RESPONSE_00_CODE("00"),
	
	/**认证失败**/
	RESPONSE_01_CODE("01"),
	
	/**失败**/
	RESPONSE_99_CODE("99"),
	
	
	/**字典服务编码**/
	CODE_DICT_DAILY_STORE_REPORT("DAILY_STORE_REPORT"),
	/**订单警报编码**/
	CODE_DICT_ORDER_ALARM_REPORT("ORDER_ALARM_REPORT"),
	/**退单警报编码**/
	CODE_DICT_REJECT_ALARM_REPORT("REJECT_ALARM_REPORT")
	
	
	
	;
	
	String value;
	
	public String getValue() {
		return value;
	}
	
	private CodeEnum(String value) {
		this.value = value; 
	}
	
}
