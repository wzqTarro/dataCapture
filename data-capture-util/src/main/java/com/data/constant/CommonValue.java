package com.data.constant;

/**
 * 字符串常量类
 * @author Alex
 * @update Tarro 2018年8月18日
 *
 */
public class CommonValue {

	public static final int PAGE = 1;
	
	public static final int SIZE = 10;
	
	public static final String DELETE_OPERATE = "delete";
	
	public static final String RECOVERY_OPERATE = "recovery";
	
	/**全局用户id标识**/
	public static final String USER_ID = "userId";
	
	/**全局jwt生成token字段标识**/
	public static final String ACCESS_TOKEN_KEY = "accessToken";
	
	/**token前面自定义标识**/
	public static final String ELLE = "elle ";
	
	/**excel2003最大支持列数**/
	public static final int MAX_COL_COUNT_2003 = 256;
	
	/**excel2003最大支持行数**/
	public static final int MAX_ROW_COUNT_2003 = 65536;
	
	/**excel2007最大支持列数**/
	public static final int MAX_COL_COUNT_2007 = 16384;
	
	/**excel2007最大支持行数**/
	public static final int MAX_ROW_COUNT_2007 = 1048576;
	
	/**门店销售日报表表头信息**/
	public static final String[] STORE_DAILY_REPORT = {"系统", "大区", "省区", "门店名称", "门店负责人"};
	
	/**订单警报集合头部**/
	public static final String[] ORDER_ALARM_REPORT_HEADER = {"系统编号", "系统名称", "大区", "省区", "门店编号", "门店名称", "单品编号", "单品条码", "单据编号", "单品名称", "存货编码", "含税进价", "含税金额", "促销供价", "合同供价", "警报"};
	
	/**退单集合头部**/
	public static final String[] REJECT_ALARM_REPORT_HEADER = {"系统编号", "系统名称", "大区", "省区", "退单机构编号", "退单机构名称", "单品编号", "单品条码", "单据编号", "单品名称", "存货编码", "含税退价", "含税金额", "促销供价", "合同供价", "警报"};
}
