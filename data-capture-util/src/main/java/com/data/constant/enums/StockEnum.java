package com.data.constant.enums;


/**
 * 库存字段枚举类
 * @author Tarro
 *
 */
public enum StockEnum {
	
	SYS_ID("系统编号", "sys_id", "getSysId"),

	SYS_NAME("系统名称", "sys_name", "getSysName"),

    STORE_CODE("门店编号", "store_code", "getStoreCode"),

    STORE_NAME("门店名称", "store_name", "getStoreName"),

    REGION("大区", "region", "getRegion"),

    PROVINCE_AREA("省区", "province_area", "getProvinceArea"),

    ASCRIPTION("归属", "ascription", "getAscription"),

    ASCRIPTION_SOLE("业绩归属", "ascription_sole", "getAscriptionSole"),

    SIMPLE_CODE("单品编号", "simple_code", "getSimpleCode"),
    
    SIMPLE_BAR_CODE("单品条码", "simple_bar_code", "getSimpleBarCode"),

    STOCK_CODE("库存编码", "stock_code", "getStockCode"),

    SIMPLE_NAME("单品名称", "simple_name", "getSimpleName"),

    BRAND("品牌", "brand", "getBrand"),

    CLASSIFY("分类", "classify", "getClassify"),

    SERIES("系列", "series", "getSeries"),

    DAY_NIGHT("日夜", "day_night", "getDayNight"),

    MATERIAL("材质", "material", "getMaterial"),

    PIECES_NUM("片数", "pieces_num", "getPiecesNum"),

    BOX_STANDARD("箱规", "box_standard", "getBoxStandard"),

    STOCK_NO("货号", "stock_no", "getStockNo"),

    STOCK_NUM("库存数量", "stock_num", "getStockNum"),

    TAX_COST_PRICE("含税成本价", "tax_cost_price", "getTaxCostPrice"),

    STOCK_PRICE("库存价格", "stock_price", "getStockPrice"),

    CREATE_TIME("日期", "create_time", "getCreateTime"),

    REMARK("备注", "remark", "getRemark");
	
	private String msg;
	private String column;
	private String methodName;
	
	private StockEnum(String msg, String column, String methodName) {
		this.msg = msg;
		this.column = column;
		this.methodName = methodName;
	}
	
	
	public String getMsg() {
		return msg;
	}


	public String getColumn() {
		return column;
	}


	public String getMethodName() {
		return methodName;
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
