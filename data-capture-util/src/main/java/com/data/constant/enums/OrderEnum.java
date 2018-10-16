package com.data.constant.enums;

public enum OrderEnum implements ICommonEnum {
	SYS_ID("系统编号", "sys_id", "getSysId"),
	
	SYS_NAME("系统名称", "sys_name", "getSysName"),
	
	REGION("大区", "region", "getRegion"),

    PROVINCE_AREA("省区", "province_area", "getProvinceArea"),
    
    SIMPLE_CODE("单品编码", "simple_code", "getSimpleCode"),
    
    SIMPLE_BAR_CODE("单品条码", "simple_bar_code", "getSimpleBarCode"),

    STOCK_CODE("存货编码", "stock_code", "getStockCode"),

    SIMPLE_NAME("单品名称", "simple_name", "getSimpleName"),
    
    STORE_CODE("门店编号", "store_code", "getStoreCode"),

    STORE_NAME("送货地点", "store_name", "getStoreName"),
    
    RECEIPT_CODE("单据号码", "receipt_code", "getReceiptCode"),
    
    TAX_RATE("税率", "tax_rate", "getTaxRate"),
    
    BOX_STANDARD("箱装规格", "box_standard", "getBoxStandard"),
    
    SIMPLE_AMOUNT("单品数量", "simple_amount", "getSimpleAmount"),
    
    BUY_PRICE_WITH_RATE("含税进价", "buying_price_with_rate", "getBuyingPriceWithRate"),
    
    BUY_PRICE("含税金额", "buying_price", "getBuyingPrice"),
    
    DELIVER_START_DATE("送货日期", "deliver_start_date", "getDeliverStartDate"),
    
    DELIVER_END_DATE("送货截止日期", "deliver_end_date", "getDeliverEndDate"),
    
    DISCOUNT_PRICE("促销供价", "discount_price", "getDiscountPrice"),
    
    DISCOUNT_START_DATE("促销供价开始日期", "discount_start_date", "getDiscountStartDate"),
    
    DISCOUNT_END_DATE("促销供价结束日期", "discount_end_date", "getDiscountEndDate"),
    
    ORDER_EFFECTIVE_JUDGE("订单有效性判断", "order_effective_judge", "getOrderEffectiveJudge"),
    
    BALANCE_WAY("补差方式", "balance_way", "getBalanceWay"),
    
    DIFF_PRICE_DISCOUNT("促销供价差异", "diff_price_discount", "getDiffPriceDiscount"),
    
    DIFF_PRICE_DISCOUNT_TOTAL("促销供价差异汇总", "diff_price_discount_total", "getDiffPriceDiscountTotal"),
    
    DISCOUNT_ALARM_FLAG("促销供价报警标志", "discount_alarm_flag", "getDiscountAlarmFlag"),
    
    CONTRACT_PRICE("合同供价", "contract_price", "getContractPrice"),
    
    DIFF_PRICE_CONTRACT("合同供价差异", "diff_price_contract", "getDiffPriceContract"),
    
    DIFF_PRICE_CONTRACT_TOTAL("合同供价差异汇总", "diff_price_contract_total", "getDiffPriceContractTotal"),
    
    CONTRACT_ALARM_FLAG("合同供价报警标志", "contract_alarm_flag", "getContractAlarmFlag"),
    
    DIFF_PRICE("汇总供价差异", "diff_price", "getDiffPrice"),
    
    REMARK("备注", "remark", "getRemark")
    
    ;
    
    private String msg;
	private String column;
	private String methodName;
	
	private OrderEnum(String msg, String column, String methodName) {
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
