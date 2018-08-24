package com.data.constant;

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
	GRAB_DATA_IS_NULL("抓取数据为空"),
	GRAB_DATA_TO_LIST_ERROR("数据转化List失败")
	;
	
	String value;
	
	public String getValue() {
		return value;
	}
	
	private TipsEnum(String value) {
		this.value = value;
	}
}
