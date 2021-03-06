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
	
	/**按时间查询销售信息**/
	public static final String QUERY_STORE_BY_SALE_DATE = "SaleMapper.queryStoreBySaleDate";
	
	/**按区域 区域一级报表**/
	public static final String QUERY_SALE_REPORT_BY_REGION = "SaleMapper.querySaleReportByRegion";
	
	/**按区域 区域一级表 月销售**/
	public static final String QUERY_SALE_REPORT_BY_DATE = "SaleMapper.querySaleReportByDate";
	
	/**按区域 区域一级表 条件查询**/
	public static final String QUERY_SALE_REPORT_BY_PARAMS = "SaleMapper.querySaleReportByParams";
	
	/**按区域 区域二级表**/
	public static final String QUERY_SALE_REPORT_BY_PROVINCE_AREA = "SaleMapper.querySaleReportByProvinceArea";
	
	/**按区域 区域二级表 月销售**/
	public static final String QUERY_SALE_REPORT_BY_PROVINCE_AREA_AND_DATE = "SaleMapper.querySaleReportByProvinceAreaAndDate";
	
	/**按区域 区域二级表 条件查询**/
	public static final String QUERY_SALE_REPORT_SECOND_BY_PARAMS = "SaleMapper.querySaleReportSecondByParams";
	
	/**按区域 区域三级表**/
	public static final String QUERY_SALE_REPORT_BY_STORE_CODE = "SaleMapper.querySaleReportByStoreCode";
	
	/**按区域 区域三级表 月销售**/
	public static final String QUERY_SALE_REPORT_BY_STORE_CODE_AND_DATE = "SaleMapper.querySaleReportByStoreCodeAndDate";
	
	/**按区域 区域三级表 条件查询**/
	public static final String QUERY_SALE_REPORT_THIRD_BY_PARAMS = "SaleMapper.querySaleReportThirdByParams";
	
	/**查询门店信息**/
	public static final String QUERY_STORE_INFO_BY_ID = "TemplateStoreMapper.queryStoreInfoById";
	
	/**查询产品信息**/
	public static final String QUERY_PRODUCT_INFO_BY_ID = "TemplateProductMapper.queryProductInfoById";
	
	/**查询供应商信息**/
	public static final String QUERY_SUPPLY_INFO_BY_ID = "TemplateSupplyMapper.querySupplyInfoById";
	
	/**查询标准条码信息**/
	public static final String QUERY_SIMPLE_CODE_INFO_BY_ID = "SimpleCodeMapper.querySimpleCodeInfoById";
	
	/**查询促销明细信息**/
	public static final String QUERY_PROMOTION_DETAIL_INFO_BY_ID = "PromotionDetailMapper.queryPromotionDetailInfoById";
	
	/**根据用工号查询用户角色**/
	public static final String QUERY_USER_ROLE_BY_WORK_NO = "SystemUserRoleMapper.queryUserRoleByWorkNo";
	
	/**根据角色查询目录集合**/
	public static final String QUERY_ROLE_MENU_BY_ROLE_ID = "SystemMenuMapper.queryRoleMenuByRoleId";
	
	/**根据角色查询按钮动作集合**/
	public static final String QUERY_FUNCTION_BY_ROLE_ID = "SystemFunctionMapper.queryFunctionByRoleId";
	
	/**根据目录编号查询数量**/
	public static final String QUERY_COUNT_MENU_BY_MENU_ID = "SystemMenuMapper.queryCountMenuByMenuId";
	
	/**查询目录总数**/
	public static final String QUERY_COUNT_MENU_BY_PAGE = "SystemMenuMapper.queryCountMenuByPage";
	
	/**查询目录集合**/
	public static final String QUERY_MENU_LIST_BY_PAGE = "SystemMenuMapper.queryMenuListByPage";
	
	/**根据目录编号查询动作按钮**/
	public static final String QUERY_MENU_FUNCTION_LIST_BY_MENU_ID = "SystemFunctionMapper.queryMenuFunctionListByMenuId";
	
	/**根据角色查询动作权限按钮**/
	public static final String QUERY_ROLE_FUNCTION_LIST_BY_ROLE_ID = "SystemFunctionMapper.queryRoleFunctionListByRoleId";
	
	/**查询角色分页总数**/
	public static final String QUERY_COUNT_ROLE_BY_PAGE = "SystemRoleMapper.queryCountRoleByPage";
	
	/**查询角色分页列表**/
	public static final String QUERY_ROLE_LIST_BY_PAGE = "SystemRoleMapper.queryRoleListByPage";
	
	/**根据角色id查询角色数量**/
	public static final String QUERY_COUNT_ROLE_BY_ROLE_ID = "SystemRoleMapper.queryCountRoleByRoleId";
	
	/**根据角色id查询角色信息**/
	public static final String QUERY_ROLE_BY_ROLE_ID = "SystemRoleMapper.queryRoleByRoleId";
	
	/**根据角色id查询角色权限数量**/
	public static final String QUERY_COUNT_ROLE_FUNCTION_BY_ROLE_ID = "SystemRoleFunctionMapper.queryCountRoleFunctionByRoleId";
	
	/**查询所有权限集合**/
	public static final String QUERY_ALL_FUNCTION_LIST = "SystemFunctionMapper.queryAllFunctionList";
	
	/**查询权限集合分页条数**/
	public static final String QUERY_COUNT_FUNCTION_PAGE = "SystemFunctionMapper.queryCountFunctionPage";
	
	/**查询权限集合分页集合**/
	public static final String QUERY_LIST_FUNCTION_PAGE = "SystemFunctionMapper.queryListFunctionPage";
	
	/**根据权限编号查询权限明细**/
	public static final String QUERY_FUNCTION_BY_FUNCTION_ID = "SystemFunctionMapper.queryFunctionByFunctionId";
	
	/**根据权限表示查询权限**/
	public static final String QUERY_FUNCTION_BY_FUNCTION_AUTH = "SystemFunctionMapper.queryFunctionByFunctionAuth";
	
	/**查询目录集合**/
	public static final String QUERY_MENU_LIST = "SystemMenuMapper.queryMenuList";
	
	/**根据角色编号查询所拥有的目录编号集合**/
	public static final String QUERY_MENU_ID_LIST_BY_ROLE_ID = "SystemRoleMenuMapper.queryMenuIdListByRoleId";
	
	public static final String QUERY_PROMOTION_STORE_LIST_BY_DETAIL_ID = "PromotionStoreListMapper.selectByPromotionDetailId";
	
	/**查询父级目录id集合**/
	public static final String QUERY_PARENT_MENU_ID_LIST = "SystemMenuMapper.queryParentMenuIdList";
	
	//查询可用供应商id集合
	public static final String QUERY_SUPPLY_IDS_LIST = "TemplateSupplyMapper.querySupplyIds";

	//条件查询抓取日志列表
	public static final String QUERY_DATA_LOG_BY_CONDITION = "DataLogMapper.queryDataLogByCondition";

	//条件查抓取日志总数
	public static final String QUERY_COUNT_DATA_LOG = "DataLogMapper.queryCountDataLog";
}
