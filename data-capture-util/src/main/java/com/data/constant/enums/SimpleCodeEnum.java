package com.data.constant.enums;

public enum SimpleCodeEnum {
	
	/**门店字段**/
	BBG("步步高", "bbg"),
	RRL("人人乐", "rrl"),
	JRD("家润多", "jrd"),
	YC("易初", "yc"),
	HNTH("湖南天虹", "hnth"),
	OS("欧尚", "os"),
	BL("宾隆", "bl"),
	BJHL("北京华联", "bjhl"),
	WS("武商", "ws"),
	GCS("冠超市", "gcs"),
	OYCB("欧亚车百", "oycb"),
	JH("佳惠", "jh"),
	DRF("大润发", "drf"),
	WEM("沃尔玛", "wem"),
	ZB("中百", "zb"),
	CB("重百", "cb")
	
	
	
	;

	String msg;
	String value;
	
	private SimpleCodeEnum(String msg, String value) {
		this.msg = msg;
		this.value = value;
	}

	public String getMsg() {
		return msg;
	}

	public String getValue() {
		return value;
	}
	public static SimpleCodeEnum getEnum(String msg) {
		for (SimpleCodeEnum e: SimpleCodeEnum.values()) {
			if (e.getMsg().equals(msg)) {
				return e;
			}
		}
		return null;
	}
}
