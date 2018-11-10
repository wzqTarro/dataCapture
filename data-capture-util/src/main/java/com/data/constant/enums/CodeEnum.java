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
	CODE_DICT_REJECT_ALARM_REPORT("REJECT_ALARM_REPORT"),
	/**按区域一级编码**/
	CODE_DICT_REGION_COMPANY_REPORT("REGION_COMPANY_REPORT"),
	/**按区域 区域一级报表**/
	CODE_DICT_REGION_SYSTEM_REPORT("REGION_SYSTEM_REPORT"),
	/**按区域 区域二级报表**/
	CODE_DICT_REGION_PROVINCE_AREA_REPORT("REGION_PROVINCE_AREA_REPORT"),
	/**按区域 区域三级报表**/
	CODE_DICT_REGION_STORE_CODE_REPORT("REGION_STORE_CODE_REPORT"),
	
	/**可用code**/
	CODE_VALUE_00_ENUM("00"),
	/**不可用code**/
	CODE_VALUE_01_ENUM("01")
	
	
	;
	
	String value;
	
	public String value() {
		return value;
	}
	
	private CodeEnum(String value) {
		this.value = value; 
	}
	
}
