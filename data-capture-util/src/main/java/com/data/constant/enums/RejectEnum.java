package com.data.constant.enums;

public enum RejectEnum implements ICommonEnum{
	SYS_ID("系统编号", "sys_id", "getSysId"),
	
	SYS_NAME("系统名称", "sys_name", "getSysName"),
	
	REGION("大区", "region", "getRegion"),

    PROVINCE_AREA("省区", "province_area", "getProvinceArea"),
    
    SIMPLE_CODE("单品编号", "simple_code", "getSimpleCode"),
    
    SIMPLE_BAR_CODE("单品条码", "simple_bar_code", "getSimpleBarCode"),

    STOCK_CODE("库存编码", "stock_code", "getStockCode"),

    SIMPLE_NAME("单品名称", "simple_name", "getSimpleName"),
    
    REJECT_DEPARTMENT_ID("退货机构", "reject_department_id", "getRejectDepartmentId"),
    
    REJECT_DEPARTMENT_NAME("退货机构名称", "reject_department_name", "getRejectDepartmentName"),
    
    RECEIPT_CODE("单据号码", "receipt_code", "getReceiptCode"),
    
    TAX_RATE("税率", "tax_rate", "getTaxRate"),
    
    SIMPLE_AMOUNT("单品数量", "simple_amount", "getSimpleAmount"),
    
    REJECT_PRICE_WITH_RATE("含税退价", "reject_price_with_rate", "getRejectPriceWithRate"),
    
    REJECT_RPICE("退货金额", "reject_price", "getRejectPrice"),
    
    REJECT_DATE("退货日期", "reject_date", "getRejectDate"),
    
    DISCOUNT_PRICE("促销供价", "discount_price", "getDiscountPrice"),
    
    DISCOUNT_START_DATE("促销供价开始日期", "discount_start_date", "getDiscountStartDate"),
    
    DISCOUNT_END_DATE("促销供价结束日期", "discount_end_date", "getDiscountEndDate"),
    
    ORDER_EFFECTIVE_JUDGE("订单有效性判断", "order_effective_judge", "getOrderEffectiveJudge"),
    
    BALANCE_WAY("补差方式", "balance_way", "getBalanceWay"),
    
    DIFF_PRICE_DISCOUNT("促销供价差异", "diff_price_discount", "getDiffPriceDiscount"),
    
    DIFF_PRICE_DISCOUNT_TOTAL("促销供价差异汇总", "diff_price_discountTotal", "getDiffPriceDiscountTotal"),
    
    DISCOUNT_ALARM_FLAG("促销供价报警标志", "discount_alarm_flag", "getDiscountAlarmFlag"),
    
    CONTRACT_PRICE("合同供价", "contract_price", "getContractPrice"),
    
    DIFF_PRICE_CONTRACT("合同供价差异", "diff_price_contract", "getDiffPriceContract"),
    
    DIFF_PRICE_CONTRACT_TOTAL("合同供价差异汇总", "diff_price_contract_total", "getDiffPriceContractTotal"),
    
    CONTRACT_ALARM_FLAG("合同供价报警标志", "contract_alarm_flag", "getContractAlarmFlag"),
    
    DIFF_PRICE("汇总供价差异", "diff_price", "getDiffPrice"),
    
    REMARK("备注", "remark", "remark")
    ;
    
    private String msg;
	private String column;
	private String methodName;
	
	private RejectEnum(String msg, String column, String methodName) {
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
