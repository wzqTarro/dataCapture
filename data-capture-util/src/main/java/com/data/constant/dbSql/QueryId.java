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
	
	public static final String QUERY_STORE_BY_ANY_COLUMN = "TemplateStoreMapper.selectByAnyColumn";
	
	public static final String QUERY_SUPPLY_BY_CONDITION = "TemplateSupplyMapper.selectByCondition";
	
	public static final String QUERY_COUNT_SUPPLY_BY_CONDITION = "TemplateSupplyMapper.selectCountByCondition";
	
	public static final String QUERY_SALE_BY_PARAM = "SaleMapper.selectByParam";
	
	public static final String QUERY_COUNT_SALE_BY_PARAM = "SaleMapper.selectCountByParam";
	
	public static final String QUERY_STOCK_BY_ANY_COLUMN = "StockMapper.selectByAnyColumn";
	
	public static final String QUERY_STOCK_BY_PARAM = "StockMapper.selectByParam";
	
	public static final String QUERY_COUNT_STOCK_BY_PARAM = "StockMapper.selectCountByParam";
	
	public static final String QUERY_COUNT_USER_BY_CONDITION = "UserMapper.queryCountUserByCondition";
	
	public static final String QUERY_USER_BY_CONDITION = "UserMapper.queryUserByCondition";
	
	public static final String QUERY_USER_DETAIL = "UserMapper.queryUserDetail";
	
	public static final String QUERY_COUNT_SALE_LIST = "SaleMapper.queryCountSaleList";
	
	public static final String QUERY_PRODUCT_BY_PARAM = "TemplateProductMapper.selectByParam";
	
	public static final String QUERY_COUNT_PRODUCT_BY_PARAM = "TemplateProductMapper.selectCountByParam";
	
	public static final String QUERY_COUNT_USER_BY_USER_ID = "UserMapper.queryCountUserByUserId";
	
	public static final String QUERY_USER_BY_WORK_NO = "UserMapper.queryUserByWorkNo";

	public static final String QUERY_STORE_BY_PARAM = "TemplateStoreMapper.selectByParam";
	
	public static final String QUERY_COUNT_STORE_BY_PARAM = "TemplateStoreMapper.selectCountByParam";
	
	public static final String QUERY_SIMPLE_CODE_BY_PARAM = "SimpleCodeMapper.selectByParam";
	
	public static final String QUERY_COUNT_SIMPLE_CODE_BY_PARAM = "SimpleCodeMapper.selectCountByParam";
	
	public static final String QUERY_ORDER_BY_CONDITION = "OrderMapper.selectByCondition";
	
	public static final String QUERY_COUNT_ORDER_BY_CONDITION = "OrderMapper.selectCountByCondition";
	
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
	
}
