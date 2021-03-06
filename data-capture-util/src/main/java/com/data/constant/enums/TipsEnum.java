package com.data.constant.enums;

/**
 * 提示枚举类
 * @author Alex
 *
 */
public enum TipsEnum {
	
	OPERATE_SUCCESS("操作成功"),
	OPERATE_ERROR("操作失败"),
	OPERATE_DATA_ERROR("数据异常"),
	AUTHENTICA_ERROR("认证失败"),
	ID_ERROR("ID不能为空"),
	ID_NOT_EXIST("ID不存在"),
	GRAB_DATA_ERROR("抓取数据失败"),
	GRAB_DATA_IS_NULL("抓取数据为空"),
	GRAB_DATA_TO_LIST_ERROR("数据转化List失败"),
	DATE_IS_NULL("查询时间不能为空"),
	NOW_DATE_IS_NULL("查询时间不能为今天"),
	COLUMN_IS_NULL("自定义表头不能为空"),
	
	//供应链
	SYS_ID_IS_NULL("系统编号不能为空"),
	SYS_NAME_IS_NULL("系统名称不能为空"),
	SYS_URL_IS_NULL("系统链接不能为空"),
	SYS_LOGIN_USER_NAME_IS_NULL("系统登录账号不能为空"),
	SYS_LOGIN_PWD_IS_NULL("系统登录密码不能为空"),
	REGION_IS_NULL("区域不能为空"),
	SYS_NOT_VAL("该供应链未启用"),
	
	// 模板数据
	PRODUCT_MESSAGE_IS_NULL("单品信息模板数据缺失"),
	SIMPLE_CODE_IS_NULL("条码信息模板数据缺失"),
	STORE_MESSAGE_IS_NULL("门店信息模板数据缺失"),
	PRAC_DATE_IS_NULL("开业时间不能为空"),
	
	// 库存数据
	QUERY_DATE_IS_NULL("查询时间不能为空"),
	STORE_NAME_IS_NULL("门店名称不能为空"),
	STORE_CODE_IS_NULL("门店编号不能为空"),
	NOW_STOCK_IS_NULL("当天库存为空，请查看是否抓取过数据"),
	
	// 销售数据
	LAST_DAY_SALE_IS_NULL("前一天的销售数据为空，请确保销售数据已经抓取"),
	
	DATA_IS_NULL("数据为空"),
	GRAB_SUCCESS("抓取成功"),
	DATA_EXCEPTION("数据异常")
	;
	
	String value;
	
	public String getValue() {
		return value;
	}
	
	private TipsEnum(String value) {
		this.value = value;
	}
}
