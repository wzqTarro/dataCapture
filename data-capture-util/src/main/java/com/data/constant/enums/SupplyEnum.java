package com.data.constant.enums;

public enum SupplyEnum {
	YH_CQ("by1831463", "重庆永辉"),
	YH_HUNAN("by1834199", "湖南永辉"),
	YH_HUBEI("by1834200", "湖北永辉"),
	YH_SC("by1834201", "四川永辉"),
	YH_BJ("by1834202", "北京永辉"),
	YH_HENAN("by1834203", "河南永辉"),
	YH_HEBEI("by1834204", "河北永辉"),
	YH_AH("by1834205", "安徽永辉"),
	YH_GZ("by1834206", "贵州永辉"),
	YH_JX("by1834207", "江西永辉"),
	CQ_ZB("by1834198", "重庆中百"),
	BBG_HUNAN("by1802189", "湖南步步高"),
	BBG_SC_1("by1803263", "四川步步高"),
	BBG_SC_2("by180484", "四川步步高"),
	XSJ("by1834577", "新世纪");
	String code;
	String name;
	private SupplyEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	
}
