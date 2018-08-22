package com.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.User;
import com.data.constant.RedisAPI;
import com.data.constant.dbSql.QueryId;
import com.data.service.RedisService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.RedisUtil;

/**
 * 缓存实现类
 * 在其他实现类注入
 * @author Administrator
 *
 */
@Service
@SuppressWarnings("static-access")
public class RedisServiceImpl extends CommonServiceImpl implements RedisService {

	@Autowired
	private RedisUtil redisUtil;

	@Override
	public void setUserModel(User user) {
		String key = RedisAPI.getPrefix(RedisAPI.REDIS_USER_DATABASE, String.valueOf(user.getId()));
		if(CommonUtil.isNotBlank(key)) {
			redisUtil.setex(key, RedisAPI.EXPIRE_30_MINUTES, FastJsonUtil.objectToString(user));
		}
	}

	@Override
	public User getUserModel(String userId) {
		String key = RedisAPI.getPrefix(RedisAPI.REDIS_USER_DATABASE, userId);
		if(CommonUtil.isNotBlank(key)) {
			String json = redisUtil.get(key);
			User user = FastJsonUtil.jsonToObject(json, User.class);
			if(user == null) {
				user = (User) queryObjectByParameter(QueryId.QUERY_USER_BY_ID, userId);
				redisUtil.setex(key, RedisAPI.EXPIRE_30_MINUTES, FastJsonUtil.objectToString(user));
			}
			return user;
		}
		return null;
	}
}
