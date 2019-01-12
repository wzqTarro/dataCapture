package com.data.constant.enums;

/**
 * 公共代码编码枚举类
 * @author Alex
 *
 */
public enum CodeEnum {

	/**成功**/
	RESPONSE_00_CODE("00"),
	RESPONSE_00_DESC("操作成功"),
	
	/**认证失败**/
	RESPONSE_01_CODE("01"),
	RESPONSE_01_DESC("认证失败"),
	
	/**失败**/
	RESPONSE_99_CODE("99"),
	RESPONSE_99_DESC("系统异常"),
	
	
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
	CODE_VALUE_01_ENUM("01"),
	
	/**导入失败**/
	UPLOAD_ERROR_DESC("导入数据发生异常"),
	/**导入数据入库数据格式不正确**/
	SQL_ERROR_DESC("导入数据格式有误，如日期或者数据类型格式用字母、符号或者中文代替，请检查之后再次重试!"),
	/**导入数据为空**/
	DATA_EMPTY_ERROR_DESC("导入数据为空，请检查导入文件是否正确！"),
	/**导入格式不对**/
	EXCEL_FORMAT_ERROR_DESC("格式不符，导入失败")
	
	
	;
	
	String value;
	
	public String value() {
		return value;
	}
	
	private CodeEnum(String value) {
		this.value = value; 
	}
	
}
