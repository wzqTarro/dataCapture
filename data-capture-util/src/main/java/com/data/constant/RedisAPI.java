package com.data.constant;

import com.data.utils.CommonUtil;

/**
 * redis常用api常量类
 * @author Alex
 *
 */
public class RedisAPI {

	/**5秒**/
	private static final int EXPIRE_FIVE_SECONDS = 5;
	
	/**10秒**/
	private static final int EXPIRE_TEN_SECONDS = 10;
	
	/**60秒  一分钟**/
	private static final int EXPIRE_ONE_MINUTE = 60;
	
	/**三分钟**/
	private static final int EXPIRE_THREE_MINUTES = 60 * 3;
	
	/**五分钟**/
	private static final int EXPIRE_FIVE_MINUTES = 60 * 5;
	
	/**十分钟**/
	private static final int EXPIRE_TEN_MINUTES = 60 *10;
	
	/**三十分钟**/
	private static final int EXPIRE_THIRED_MINUTES = 60 * 30;
	
	/**一小时**/
	private static final int EXPIRE_A_HOUR = 60 * 60;
	
	/**两小时**/
	private static final int EXPIRE_TWO_HOUR = 60 * 60 * 2;
	
	/**一天**/
	private static final int EXPIRE_A_DAY = 60 * 60 * 24;
	
	/**key值分隔符**/
	private static final String REDIS_PATTERN = ":";
	
	/**订单库**/
	private static final String REDIS_ORDER_DATABASE = "order";
	
	/**销售库**/
	private static final String REDIS_SALE_DATABASE = "sale";
	
	/**库存库**/
	private static final String REDIS_ACOUNT_DATABASE = "acount";
	
	/**退货库**/
	private static final String REDIS_REJECT_DATABASE = "reject";
	
	/**
	 * 得到存入缓存中的key值
	 * @param database 存入哪种类型的库
	 * @param keys 要存入的key值
	 * @return
	 */
	public static String getPrefix(String database, String...keys) {
		if(CommonUtil.isNotBlank(database) && CommonUtil.isNotBlank(keys)) {
			StringBuilder builder = new StringBuilder();
			builder.append(database);
			for(int i = 0; i < keys.length; i++) {
				builder.append(REDIS_PATTERN);
				builder.append(keys[i]);
			}
			return builder.toString();
		}
		return null;
	}
}
