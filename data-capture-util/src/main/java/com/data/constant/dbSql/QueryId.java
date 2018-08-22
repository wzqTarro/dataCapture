package com.data.constant.dbSql;

/**
 * 查询mapper常量类
 * @author Tarro
 *
 */
public class QueryId {
	
	public static final String QUERY_USER_BY_ID = "UserMapper.selectByPrimaryKey";
	
	public static final String QUERY_ORDER_BY_ID = "OrderMapper.selectByPrimaryKey";
	
	public static final String QUERY_SUPPLY_BY_ID = "SupplyMapper.selectByPrimaryKey";
	
	public static final String QUERY_PRODUCT_BY_ID = "ProductMapper.selectByPrimaryKey";
	
	public static final String QUERY_SALE_BY_ID = "SaleMapper.selectByPrimaryKey";
	
	public static final String QUERY_SIMPLE_CODE_BY_ID = "SimpleCodeMapper.selectByPrimaryKey";
	
	public static final String QUERY_STORE_BY_ID = "StoreMapper.selectByPrimaryKey";
	
	public static final String QUERY_ORDER_BY_PARAM = "OrderMapper.selectByParam";
	
	public static final String QUERY_ORDER_BY_CONDITION = "OrderMapper.selectByCondtion";
	
	public static final String QUERY_COUNT_ORDER_BY_CONDITION = "OrderMapper.selectCountByCondtion";
	
	public static final String QUERY_SUPPLY_BY_CONDITION = "SupplyMapper.selectByCondition";
	
	public static final String QUERY_COUNT_SUPPLY_BY_CONDITION = "SupplyMapper.selectCountByCondition";
	
	public static final String QUERY_SALE_BY_PARAM = "SaleMapper.selectByParam";
	
	public static final String QUERY_COUNT_SALE_BY_PARAM = "SaleMapper.selectCountByParam";
	
}
