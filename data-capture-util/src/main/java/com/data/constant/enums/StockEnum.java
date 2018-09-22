package com.data.constant.enums;


/**
 * 库存字段枚举类
 * @author Tarro
 *
 */
public enum StockEnum {

	SYS_NAME("系统名称", "sys_name"),

    STORE_CODE("门店编号", "store_code"),

    STORE_NAME("门店名称", "store_name"),

    REGION("大区", "region"),

    PROVINCE_AREA("省区", "province_area"),

    ASCRIPTION("归属", "ascription"),

    ASCRIPTION_SOLE("业绩归属", "ascription_sole"),

    SIMPLE_CODE("单品编号", "simple_code"),
    
    SIMPLE_BAR_CODE("单品条码", "simple_bar_code"),

    STOCK_CODE("库存编码", "stock_code"),

    SIMPLE_NAME("单品名称", "simple_name"),

    BRAND("品牌", "brand"),

    CLASSIFY("分类", "classify"),

    SERIES("系列", "series"),

    DAY_NIGHT("日夜", "day_night"),

    MATERIAL("材质", "material"),

    PIECES_NUM("片数", "pieces_num"),

    BOX_STANDARD("箱规", "box_standard"),

    STOCK_NO("货号", "stock_no"),

    STOCK_NUM("库存数量", "stock_num"),

    TAX_COST_PRICE("含税成本价", "tax_cost_price"),

    STOCK_PRICE("库存价格", "stock_price"),

    CREATE_TIME("日期", "create_time"),

    REMARK("备注", "remark");
	
	private String msg;
	private String value;
	
	private StockEnum(String msg, String value) {
		this.msg = msg;
		this.value = value;
	}
	
	
	public String getMsg() {
		return msg;
	}


	public String getValue() {
		return value;
	}


	public static StockEnum getEnum(String msg) {
		for(StockEnum e : StockEnum.values()) {
			if (e.getMsg().equals(msg)) {
				return e;
			}
		}
		return null;
	}
}
