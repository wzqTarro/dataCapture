package com.data.constant.dbSql;

/**
 * 查询mapper常量类
 * @author Tarro
 *
 */
public final class QueryId {
	
	public static final String QUERY_USER_BY_ID = "UserMapper.selectByPrimaryKey";
	
	public static final String QUERY_SUPPLY_BY_ID = "TemplateSupplyMapper.selectByPrimaryKey";
	
	public static final String QUERY_ORDER_BY_ID = "OrderMapper.selectByPrimaryKey";
	
	public static final String QUERY_PRODUCT_BY_ID = "TemplateProductMapper.selectByPrimaryKey";
	
	public static final String QUERY_SALE_BY_ID = "SaleMapper.selectByPrimaryKey";
	
	public static final String QUERY_STOCK_BY_ID = "StockMapper.selectByPrimaryKey";
	
	public static final String QUERY_SIMPLE_CODE_BY_ID = "SimpleCodeMapper.selectByPrimaryKey";
	
	public static final String QUERY_STORE_BY_ID = "TemplateStoreMapper.selectByPrimaryKey";
	
	/**获取大区菜单**/
	public static final String QUERY_REGION_MENU = "TemplateStoreMapper.selectRegionMenu";
	
	/**获取门店菜单**/
	public static final String QUERY_STORE_MENU = "TemplateStoreMapper.selectStoreMenu";
	
	public static final String QUERY_SUPPLY_BY_CONDITION = "TemplateSupplyMapper.selectByCondition";
	
	public static final String QUERY_COUNT_SUPPLY_BY_CONDITION = "TemplateSupplyMapper.selectCountByCondition";
	
	public static final String QUERY_SALE_BY_PARAM = "SaleMapper.selectByParam";
	
	public static final String QUERY_COUNT_SALE_BY_PARAM = "SaleMapper.selectCountByParam";
	
	public static final String QUERY_STOCK_BY_ANY_COLUMN = "StockMapper.selectByAnyColumn";
	
	public static final String QUERY_ORDER_BY_ANY_COLUMN = "OrderMapper.selectByAnyColumn";
	
	public static final String QUERY_SALE_BY_ANY_COLUMN = "SaleMapper.selectByAnyColumn";
	
	public static final String QUERY_REJECT_BY_ANY_COLUMN = "RejectMapper.selectByAnyColumn";
	
	public static final String QUERY_STOCK_BY_PARAM = "StockMapper.selectByParam";
	
	public static final String QUERY_COUNT_STOCK_BY_PARAM = "StockMapper.selectCountByParam";
	
	public static final String QUERY_COUNT_USER_BY_CONDITION = "UserMapper.queryCountUserByCondition";
	
	public static final String QUERY_USER_BY_CONDITION = "UserMapper.queryUserByCondition";
	
	public static final String QUERY_USER_DETAIL = "UserMapper.queryUserDetail";
	
	public static final String QUERY_COUNT_SALE_LIST = "SaleMapper.queryCountSaleList";
	
	public static final String QUERY_PRODUCT_BY_PARAM = "TemplateProductMapper.selectByParam";
	
	public static final String QUERY_COUNT_PRODUCT_BY_PARAM = "TemplateProductMapper.selectCountByParam";
	
	/**获取品牌菜单**/
	public static final String QUERY_PRODUCT_BRAND = "TemplateProductMapper.selectBrandMenu";
	
	public static final String QUERY_COUNT_USER_BY_USER_ID = "UserMapper.queryCountUserByUserId";
	
	public static final String QUERY_USER_BY_WORK_NO = "UserMapper.queryUserByWorkNo";

	public static final String QUERY_STORE_BY_PARAM = "TemplateStoreMapper.selectByParam";
	
	public static final String QUERY_COUNT_STORE_BY_PARAM = "TemplateStoreMapper.selectCountByParam";
	
	public static final String QUERY_SIMPLE_CODE_BY_PARAM = "SimpleCodeMapper.selectByParam";
	
	public static final String QUERY_COUNT_SIMPLE_CODE_BY_PARAM = "SimpleCodeMapper.selectCountByParam";
	
	public static final String QUERY_ORDER_BY_CONDITION = "OrderMapper.selectByCondition";
	
	public static final String QUERY_COUNT_ORDER_BY_CONDITION = "OrderMapper.selectCountByCondition";
	
	public static final String QUERY_REJECT_BY_PARAM = "RejectMapper.selectByParam";
	
	public static final String QUERY_COUNT_REJECT_BY_PARAM = "RejectMapper.selectCountByParam";
	
	public static final String QUERY_PROMOTION_DETAIL_BY_PARAM = "PromotionDetailMapper.selectByParam";
	
	public static final String QUERY_COUNT_PROMOTION_DETAIL_BY_PARAM = "PromotionDetailMapper.selectCountByParam";
	
	/**销售日报表导出**/
	public static final String QUERY_SALE_LIST_REPORT = "SaleMapper.querySaleListReport";
	
	/**查询销售日报表总数**/
	public static final String QUERY_COUNT_SALE_LIST_REPORT = "SaleMapper.queryCountSaleListReport";
	
	/**查询销售表信息**/
	public static final String QUERY_SALE_MESSAGE_LIST = "SaleMapper.querySaleMessageList";
	
	/**根据门店编号查询销售表信息**/
	public static final String QUERY_SALE_INFO_BY_STORE_CODE = "SaleMapper.querySaleInfoByStoreCode";
	
	/**查询门店模板集合**/
	public static final String QUERY_STORE_TEMPLATE = "TemplateStoreMapper.queryStoreTemplate";
	
	/**查询条码模板集合**/
	public static final String QUERY_SIMPLE_CODE_TEMPLATE = "SimpleCodeMapper.querySimpleCodeTemplate";
	
	/**查询产品模板集合**/
	public static final String QUERY_PRODUCT_TEMPLATE = "TemplateProductMapper.queryProductTemplate";
	
	/**根据服务编号查询对应字典参数**/
	public static final String QUERY_CODE_DICT_LIST_BY_SERVICE_CODE = "CodeDictMapper.queryCodeDictListByServiceCode";
	
	/**按门店分组查询库存**/
	public static final String QUERY_STORE_STOCK_MODEL_BY_PARAM = "StockMapper.selectStoreStockModelByParam";
	
	/**按大区分组查询库存**/
	public static final String QUERY_REGION_STOCK_MODEL_BY_PARAM = "StockMapper.selectRegionStockModelByParam";
	
	/**按系统分组查询库存**/
	public static final String QUERY_SYS_STOCK_MODEL_BY_PARAM = "StockMapper.selectSysStockModelByParam";
	
	/**按省区分组查询库存**/
	public static final String QUERY_PROVINCE_MODEL_BY_PARAM = "StockMapper.selectProvinceAreaStockModelByParam";
	
	/**查询供应链部分字段**/
	public static final String QUERY_SUPPLY_ANY_COLUMN = "TemplateSupplyMapper.selectAnyColumn";

	/**查詢門店編碼集合**/
	public static final String QUERY_STORE_CODE_LIST = "SaleMapper.queryStoreCodeList";
	
	/**按组查询门店日销售额**/
	public static final String QUERY_DAILY_STORE_SALE_LIST_BY_GROUP = "SaleMapper.queryStoreSaleListByGroup";
	
	/**条件查询订单警报集合**/
	public static final String QUERY_ORDER_ALARM_LIST = "OrderMapper.queryOrderAlarmList";
	
	/**查询订单警报总和**/
	public static final String QUERY_COUNT_ORDER_ALARM_LIST = "OrderMapper.queryCountOrderAlarmList";
	
	/**条件查询退单警报集合**/
	public static final String QUERY_REJECT_ALARM_LIST = "RejectMapper.queryRejectAlarmList";
	
	/**查询退单警报总和**/
	public static final String QUERY_COUNT_REJECT_ALARM_LIST = "RejectMapper.queryCountRejectAlarmList";
	
	/**查询订单警报列表集合**/
	public static final String QUERY_ORDER_ALARM_LIST_FOR_REPORT = "OrderMapper.queryOrderAlarmListForReport";
	
	/**查询退单警报列表集合**/
	public static final String QUERY_REJECT_ALARM_LIST_FOR_REPORT = "RejectMapper.queryRejectAlarmListForReport";
	
	/**查询区域门店数量**/
	public static final String QUERY_STORE_NUM_BY_REGION = "SaleMapper.queryStoreNumByRegion";
	
	/**查询区域月销售额**/
	public static final String QUERY_REGION_SALE_BY_DATE = "SaleMapper.queryRegionSaleByDate";

	/**按系统分组查询销售数据**/
	public static final String QUERY_SYS_SALE_BY_CONDTION = "SaleMapper.selectSysSaleByCondition";
	
	/**按大区分组查询销售数据**/
	public static final String QUERY_REGION_SALE_BY_CONDITION = "SaleMapper.selectRegionSaleByCondition";
	
	/**按门店分组查询销售数据**/
	public static final String QUERY_STORE_SALE_BY_CONDITION = "SaleMapper.selectStoreSaleByCondition";
	
	/**查询上个月或去年今日销售额**/
	public static final String QUERY_REGION_SALE_BY_DATE_STR = "SaleMapper.queryRegionSaleByDateStr";
	
	/**查询当前门店**/
	public static final String QUERY_NOW_STORE_CODE = "SaleMapper.queryNowStoreCode";
}
