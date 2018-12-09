package com.data.constant.dbSql;

/**
 * 更新mapper常量类
 * @author Tarro
 *
 */
public final class UpdateId {

	public static final String UPDATE_USER_MESSAGE = "UserMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_ORDER_MESSAGE = "OrderMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_SUPPLY_MESSAGE = "TemplateSupplyMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_ORDER_MESSAGE_BY_ORDER_ID = "TemplateOrderMapper.updateByOrderIdSelective";
	
	public static final String UPDATE_PRODUCT_BY_MESSAGE = "TemplateProductMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_SALE_BY_MESSAGE = "SaleMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_SIMPLE_CODE_BY_MESSAGE = "SimpleCodeMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_STORE_BY_MESSAGE = "TemplateStoreMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_USER_MESSAGE_BY_WORK_NO = "UserMapper.updateUserMessageByWorkNo";
	
	public static final String UPDATE_STOCK_BY_MESSAGE = "StockMapper.updateByPrimaryKeySelective";
	
	public static final String UPDATE_PROMOTION_DETAIL_BY_MESSAGE = "PromotionDetailMapper.updateByPrimaryKeySelective";
	
	/**根据目录id更新目录信息**/
	public static final String UPDATE_MENU_BY_MENU_ID = "SystemMenuMapper.updateMenuByMenuId";
	
	/**根据角色id更新角色信息**/
	public static final String UPDATE_ROLE_BY_ROLE_ID = "SystemRoleMapper.updateRoleByRoleId";
	
	public static final String UPDATE_SYSTEM_FUNCTION_BY_FUNCTION_ID = "SystemFunctionMapper.updateSystemFunctionByFunctionId";
}
