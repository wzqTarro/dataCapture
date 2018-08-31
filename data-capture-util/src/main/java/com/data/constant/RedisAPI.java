package com.data.constant;

import com.data.utils.CommonUtil;

/**
 * redis常用api常量类
 * @author Alex
 *
 */
public class RedisAPI {

	/**5秒**/
	public static final int EXPIRE_5_SECONDS = 5;
	
	/**10秒**/
	public static final int EXPIRE_10_SECONDS = 10;
	
	/**60秒  一分钟**/
	public static final int EXPIRE_1_MINUTE = 60;
	
	/**三分钟**/
	public static final int EXPIRE_3_MINUTES = 60 * 3;
	
	/**五分钟**/
	public static final int EXPIRE_5_MINUTES = 60 * 5;
	
	/**十分钟**/
	public static final int EXPIRE_10_MINUTES = 60 *10;
	
	/**三十分钟**/
	public static final int EXPIRE_30_MINUTES = 60 * 30;
	
	/**一小时**/
	public static final int EXPIRE_1_HOUR = 60 * 60;
	
	/**两小时**/
	public static final int EXPIRE_2_HOUR = 60 * 60 * 2;
	
	/**一天**/
	public static final int EXPIRE_24_HOUR = 60 * 60 * 24;
	
	/**key值分隔符**/
	public static final String REDIS_PATTERN = ":";
	
	/**订单库**/
	public static final String REDIS_ORDER_DATABASE = "order";
	
	/**销售库**/
	public static final String REDIS_SALE_DATABASE = "sale";
	
	/**库存库**/
	public static final String REDIS_STORE_DATABASE = "store";
	
	/**退货库**/
	public static final String REDIS_REJECT_DATABASE = "reject";
	
	/**用户库**/
	public static final String REDIS_USER_DATABASE = "user";
	
	public static final String REDIS_TOKEN_AUTHENTICATE = "data:token";
	
	/**
	 * 得到存入缓存中的key值
	 * @param database 存入哪种类型的库
	 * @param keys 要存入的key值
	 * @return
	 */
	public static String getPrefix(String prefix, String...keys) {
		if(CommonUtil.isNotBlank(prefix) && CommonUtil.isNotBlank(keys)) {
			StringBuilder builder = new StringBuilder();
			builder.append(prefix);
			for(int i = 0; i < keys.length; i++) {
				builder.append(REDIS_PATTERN);
				builder.append(keys[i]);
			}
			return builder.toString();
		}
		return null;
	}
}
