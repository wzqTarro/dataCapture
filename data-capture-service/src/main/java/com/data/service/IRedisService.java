package com.data.service;

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

}
