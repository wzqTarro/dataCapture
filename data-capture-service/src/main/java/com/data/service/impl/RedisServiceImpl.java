package com.data.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.User;
import com.data.constant.RedisAPI;
import com.data.constant.dbSql.QueryId;
import com.data.service.IRedisService;
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
public class RedisServiceImpl extends CommonServiceImpl implements IRedisService {

	@Autowired
	private RedisUtil redisUtil;

	@Override
	public void saveUserModel(User user) {
		String key = RedisAPI.getPrefix(RedisAPI.REDIS_USER_DATABASE, user.getWorkNo());
		if(CommonUtil.isNotBlank(key)) {
			redisUtil.setex(key, RedisAPI.EXPIRE_30_MINUTES, FastJsonUtil.objectToString(user));
		}
	}

	@Override
	public User getUserModel(String workNo) {
		String key = RedisAPI.getPrefix(RedisAPI.REDIS_USER_DATABASE, workNo);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			User user = FastJsonUtil.jsonToObject(json, User.class);
			if(user == null) {
				user = (User) queryObjectByParameter(QueryId.QUERY_USER_BY_WORK_NO, workNo);
				redisUtil.setex(key, RedisAPI.EXPIRE_30_MINUTES, FastJsonUtil.objectToString(user));
			}
			return user;
		}
		return null;
	}

	@Override
	public void deleteUserModel(String workNo) {
		String key = RedisAPI.getPrefix(RedisAPI.REDIS_USER_DATABASE, workNo);
		if(CommonUtil.isNotBlank(key)) {
			redisUtil.del(key);
		}
	}

	@Override
	public void updateUserModel(User user) {
		deleteUserModel(user.getWorkNo());
		saveUserModel(user);
	}

	@Override
	public void setUserId(String userId, String token) {
		String key = RedisAPI.getPrefix(RedisAPI.REDIS_USER_DATABASE, token);
		redisUtil.setex(key, RedisAPI.EXPIRE_30_MINUTES, userId);
	}

	@Override
	public String getUserId(String token) {
		String key = RedisAPI.getPrefix(RedisAPI.REDIS_USER_DATABASE, token);
		return redisUtil.get(key);
	}

	@Override
	public void setAccessToken(String key, String token) {
		String tokenKey = RedisAPI.getPrefix(RedisAPI.REDIS_TOKEN_AUTHENTICATE, key);
		redisUtil.setex(tokenKey, RedisAPI.EXPIRE_30_MINUTES, token);
	}

	@Override
	public String getAccessToken(String key) {
		String tokenKey = RedisAPI.getPrefix(RedisAPI.REDIS_TOKEN_AUTHENTICATE, key);
		return redisUtil.get(tokenKey);
	}

	@Override
	public void deleteAccessToken(String key) {
		String tokenKey = RedisAPI.getPrefix(RedisAPI.REDIS_TOKEN_AUTHENTICATE, key);
		redisUtil.del(tokenKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> querySaleDateMessageByStore(String dateStr) {
		String key = RedisAPI.getPrefix(RedisAPI.DAILY_STORE_SALE_PREFIX, dateStr);
		String json = redisUtil.get(key);
		
		if(CommonUtil.isBlank(json)) {
			return new ArrayList<Map<String, Object>>(10);
		}
		
		return FastJsonUtil.jsonToObject(json, List.class);
	}

	@Override
	public void setSaleDailyMessageByStore(String dateStr, List<Map<String, Object>> storeDailySaleList) {
		String key = RedisAPI.getPrefix(RedisAPI.DAILY_STORE_SALE_PREFIX, dateStr);
		redisUtil.setex(key, RedisAPI.EXPIRE_1_MONTH, FastJsonUtil.objectToString(storeDailySaleList));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> querySaleList() {
		String key = RedisAPI.getPrefix(RedisAPI.STORE_MESSAGE);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return (List<Map<String, Object>>) FastJsonUtil.jsonToList(json, List.class);
		}
		List<Map<String, Object>> saleList = queryListByObject(QueryId.QUERY_SALE_MESSAGE_LIST, null);
		if(CommonUtil.isNotBlank(saleList)) {
			redisUtil.set(key, FastJsonUtil.objectToString(saleList));
			return saleList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> querySaleInfo(String storeCode) {
		String key = RedisAPI.getPrefix(RedisAPI.STORE_MESSAGE, storeCode);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return FastJsonUtil.jsonToObject(json, Map.class);
		}
		Map<String, Object> saleMap = (Map<String, Object>) queryObjectByParameter(QueryId.QUERY_SALE_INFO_BY_STORE_CODE, storeCode);
		if(CommonUtil.isNotBlank(saleMap)) {
			redisUtil.set(key, FastJsonUtil.objectToString(saleMap));
			return saleMap;
		}
		return null;
	}
}
