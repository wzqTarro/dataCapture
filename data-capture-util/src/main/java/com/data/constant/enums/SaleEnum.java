package com.data.constant.enums;

public enum SaleEnum implements ICommonEnum{
	SYS_ID("系统编号", "sys_id", "getSysId"),
	
	SYS_NAME("系统名称", "sys_name", "getSysName"),
	
	REGION("大区", "region", "getRegion"),

    PROVINCE_AREA("省区", "province_area", "getProvinceArea"),
    
    SIMPLE_CODE("单品编号", "simple_code", "getSimpleCode"),
    
    SIMPLE_BAR_CODE("单品条码", "simple_bar_code", "getSimpleBarCode"),

    STOCK_CODE("库存编码", "stock_code", "getStockCode"),

    SIMPLE_NAME("单品名称", "simple_name", "getSimpleName"),
    
    STORE_CODE("门店编号", "store_code", "getStoreCode"),

    STORE_NAME("门店名称", "store_name", "getStoreName"),

    ASCRIPTION("归属", "ascription", "getAscription"),

    ASCRIPTION_SOLE("业绩归属", "ascription_sole", "getAscriptionSole"),

    BRAND("品牌", "brand", "getBrand"),
    
    SERIES("系列", "series", "getSeries"),

    DAY_NIGHT("日夜", "day_night", "getDayNight"),

    MATERIAL("材质", "material", "getMaterial"),

    PIECES_NUM("片数", "pieces_num", "getPiecesNum"),

    BOX_STANDARD("箱规", "box_standard", "getBoxStandard"),

    STOCK_NO("货号", "stock_no", "getStockNo"),
    
    SELL_NUM("销售数量", "sell_num", "getSellNum"),
    
    SELL_PRICE("销售价格", "sell_price", "getSellPrice"),
    
    CREATE_TIME("日期", "create_time", "getCreateTime"),
    
    STORE_MANAGER("门店负责人", "store_manager", "getStoreManager"),
    
    REMARK("备注", "remark", "getRemark")

    ;
    
    private String msg;
	private String column;
	private String methodName;
	
	private SaleEnum(String msg, String column, String methodName) {
		this.msg = msg;
		this.column = column;
		this.methodName = methodName;
	}
	
	@Override
	public String getMsg() {
		return msg;
	}

	@Override
	public String getColumn() {
		return column;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}
}
