package com.data.service;

import java.util.List;
import java.util.Map;

import com.data.bean.User;

/**
 * 缓存服务类
 * @author Alex
 *
 */
public interface IRedisService {
	
	/**
	 * 保存用户信息
	 * @param user
	 */
	void saveUserModel(User user);
	
	/**
	 * 取出用户信息
	 * @param workNo
	 * @return
	 */
	User getUserModel(String workNo);
	
	/**
	 * 更新用户信息
	 * @param user
	 */
	void updateUserModel(User user);
	
	/**
	 * 删除用户信息
	 * @param workNo
	 */
	void deleteUserModel(String workNo);
	
	/**
	 * 生成token时插入的userId 30分钟
	 * @param userId
	 */
	void setUserId(String userId, String token);
	
	/**
	 * 取token中的userId
	 * @param token
	 * @return
	 */
	String getUserId(String token);
	
	/**
	 * 保存token
	 * @param key
	 * @param token
	 */
	void setAccessToken(String key, String token);
	
	/**
	 * 得到token
	 * @param key
	 * @return
	 */
	String getAccessToken(String key);
	
	/**
	 * 删除token
	 * @param key
	 */
	void deleteAccessToken(String key);
	
	/**
	 * 查询门店日销售额信息
	 * @param dateStr
	 * @return
	 */
	List<Map<String, Object>> querySaleDateMessageByStore(String dateStr);
	
	/**
	 * 保存门店信息日销售额
	 * @param dateStr
	 */
	void setSaleDailyMessageByStore(String dateStr, List<Map<String, Object>> storeDailySaleList);
	
	/**
	 * 查询销售门店数据
	 * @return
	 */
	List<Map<String, Object>> querySaleList();
	
	/**
	 * 根据门店信息查询sale信息
	 * @param storeCode
	 * @return
	 */
	Map<String, Object> querySaleInfo(String storeCode);

}
