package com.data.constant.enums;

/**
 * excel使用的枚举类
 * @author Alex
 *
 */
public enum ExcelEnum {
	
	/**导出模板枚举**/
	ORDER_TEMPLATE_TYPE("order"),
	PROMOTION_DETAIL_TEMPLATE_TYPE("promotion_detail"),
	REJECT_TEMPLATE_TYPE("reject"),
	SALE_TEMPLATE_TYPE("sale"),
	SIMPLE_CODE_TEMPLATE_TYPE("simple_code"),
	STOCK_TEMPLATE_TYPE("stock"),
	TEMPLATE_PRODUCT_TEMPLATE_TYPE("template_product"),
	TEMPLATE_STORE_TEMPLATE_TYPE("template_store"),
	TEMPLATE_SUPPLY_TEMPLATE_TYPE("template_supply"),
	USER_TEMPLATE_TYPE("user")
	
	
	;
	
	String value;
	
	public String value() {
		return value;
	}
	
	private ExcelEnum(String value) {
		this.value = value; 
	}
}
