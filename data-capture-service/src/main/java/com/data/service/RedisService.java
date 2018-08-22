package com.data.service;

import com.data.bean.User;

/**
 * 缓存服务类
 * @author Alex
 *
 */
public interface RedisService {
	
	void setUserModel(User user);
	
	User getUserModel(String userId);

}
