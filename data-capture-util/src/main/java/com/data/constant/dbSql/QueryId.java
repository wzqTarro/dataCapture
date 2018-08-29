package com.data.constant.dbSql;

/**
 * 查询mapper常量类
 * @author Tarro
 *
 */
public class QueryId {
	
	public static final String QUERY_USER_BY_ID = "UserMapper.selectByPrimaryKey";
	
	public static final String QUERY_ORDER_BY_ID = "TemplateOrderMapper.selectByPrimaryKey";
	
	public static final String QUERY_SUPPLY_BY_ID = "TemplateSupplyMapper.selectByPrimaryKey";
	
	public static final String QUERY_PRODUCT_BY_ID = "TemplateProductMapper.selectByPrimaryKey";
	
	public static final String QUERY_SALE_BY_ID = "SaleMapper.selectByPrimaryKey";
	
	public static final String QUERY_SIMPLE_CODE_BY_ID = "SimpleCodeMapper.selectByPrimaryKey";
	
	public static final String QUERY_STORE_BY_ID = "TemplateStoreMapper.selectByPrimaryKey";
	
	public static final String QUERY_ORDER_BY_PARAM = "TemplateOrderMapper.selectByParam";
	
	public static final String QUERY_ORDER_BY_CONDITION = "TemplateOrderMapper.selectByCondtion";
	
	public static final String QUERY_COUNT_ORDER_BY_CONDITION = "TemplateOrderMapper.selectCountByCondtion";
	
	public static final String QUERY_SUPPLY_BY_CONDITION = "TemplateSupplyMapper.selectByCondition";
	
	public static final String QUERY_COUNT_SUPPLY_BY_CONDITION = "TemplateSupplyMapper.selectCountByCondition";
	
	public static final String QUERY_SALE_BY_PARAM = "TemplateSaleMapper.selectByParam";
	
	public static final String QUERY_COUNT_SALE_BY_PARAM = "SaleMapper.selectCountByParam";
	
	public static final String QUERY_COUNT_USER_BY_CONDITION = "UserMapper.queryCountUserByCondition";
	
	public static final String QUERY_USER_BY_CONDITION = "UserMapper.queryUserByCondition";
	
	public static final String QUERY_USER_DETAIL = "UserMapper.queryUserDetail";
	
	public static final String QUERY_COUNT_SALE_LIST = "SaleMapper.queryCountSaleList";
	
	public static final String QUERY_PRODUCT_BY_PARAM = "TemplateProductMapper.selectByParam";
	
	public static final String QUERY_COUNT_PRODUCT_BY_PARAM = "TemplateProductMapper.selectCountByParam";
	
	
}
