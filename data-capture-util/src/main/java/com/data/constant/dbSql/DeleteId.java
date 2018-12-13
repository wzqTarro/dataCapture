package com.data.constant.dbSql;

/**
 * 删除mapper常量类
 * @author Tarro
 *
 */
public final class DeleteId {
	
	public static final String DELETE_USER_BY_ID = "UserMapper.deleteByPrimaryKey";
	
	public static final String DELETE_ORDER_BY_ID = "OrderMapper.deleteByPrimaryKey";
	
	public static final String DELETE_SUPPLY_BY_ID = "TemplateSupplyMapper.deleteByPrimaryKey";
	
	public static final String DELETE_PRODUCT_BY_ID = "TemplateProductMapper.deleteByPrimaryKey";
	
	public static final String DELETE_SALE_BY_ID = "SaleMapper.deleteByPrimaryKey";
	
	public static final String DELETE_SIMPLE_CODE_BY_ID = "SimpleCodeMapper.deleteByPrimaryKey";
	
	public static final String DELETE_STORE_BY_ID = "TemplateStoreMapper.deleteByPrimaryKey";
	
	public static final String DELETE_STOCK_BY_ID = "StockMapper.deleteByPrimaryKey";
	
	public static final String DELETE_STOCK_BY_SYS_ID = "StockMapper.deleteBySysId";
	
	public static final String DELETE_PROMOTION_DETAIL_BY_ID = "PromotionDetailMapper.deleteByPrimaryKey";
	
	public static final String DELETE_ROLE_FUNCTION_BY_ROLE_ID = "SystemRoleFunctionMapper.deleteByRoleId";
	
	public static final String DELETE_SYSTEM_FUNCTION_BY_FUNCTION_ID = "SystemFunctionMapper.deleteSystemFunctionByFunctionId";
	
	public static final String DELETE_SYSTEM_ROLE_MENU_BY_ROLE_ID = "SystemRoleMenuMapper.deleteSystemRoleMenuByRoleId";
	
	public static final String DELETE_PROMOTION_STORE_LIST_BY_DETAIL_ID = "PromotionStoreListMapper.deleteByPromotionDetailId";
}
