package com.data.service;

import com.data.bean.User;

/**
 * 缓存服务类
 * @author Alex
 *
 */
public interface IRedisService {
	
	void saveUserModel(User user);
	
	User getUserModel(String workNo);
	
	void updateUserModel(User user);
	
	void deleteUserModel(String workNo);

}
