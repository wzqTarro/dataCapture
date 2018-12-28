package com.data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.SimpleCode;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.User;
import com.data.constant.RedisAPI;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.SimpleCodeEnum;
import com.data.service.IRedisService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.JsonUtil;
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
		redisUtil.setex(tokenKey, RedisAPI.EXPIRE_1_HOUR, token);
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
		
		return (List<Map<String, Object>>) FastJsonUtil.jsonToList(json, Map.class);
	}

	@Override
	public void setSaleDailyMessageByStore(String dateStr, List<Map<String, Object>> storeDailySaleList) throws Exception {
		String key = RedisAPI.getPrefix(RedisAPI.DAILY_STORE_SALE_PREFIX, dateStr);
		redisUtil.setex(key, RedisAPI.EXPIRE_1_MONTH, JsonUtil.toJson(storeDailySaleList));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> querySaleList() throws Exception {
		String key = RedisAPI.getPrefix(RedisAPI.STORE_MESSAGE);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return (List<Map<String, Object>>) FastJsonUtil.jsonToList(json, Map.class);
		}
		List<Map<String, Object>> saleList = queryListByObject(QueryId.QUERY_SALE_MESSAGE_LIST, null);
		if(CommonUtil.isNotBlank(saleList)) {
			redisUtil.set(key, JsonUtil.toJson(saleList));
			return saleList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> querySaleInfo(String storeCode) throws Exception {
		String key = RedisAPI.getPrefix(RedisAPI.STORE_MESSAGE, storeCode);
		String json = redisUtil.get(key);
		Map<String, Object> saleMap = new HashMap<>(8);
		if(CommonUtil.isNotBlank(json)) {
			return FastJsonUtil.jsonToObject(json, Map.class);
		}
		saleMap = (Map<String, Object>) queryObjectByParameter(QueryId.QUERY_SALE_INFO_BY_STORE_CODE, storeCode);
		if(CommonUtil.isNotBlank(saleMap)) {
			redisUtil.setex(key, RedisAPI.EXPIRE_1_MONTH, FastJsonUtil.objectToString(saleMap));
			return saleMap;
		}
		return null;
	}

	@Override
	public void setTempSaleInfo(String storeCode, Map<String, Object> saleInfoMap) throws Exception {
		String key = RedisAPI.getPrefix(RedisAPI.TEMP_STORE_INFO, storeCode);
		redisUtil.setex(key, RedisAPI.EXPIRE_1_MINUTE, FastJsonUtil.objectToString(saleInfoMap));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryTempSaleInfo(String storeCode) throws Exception {
		String key = RedisAPI.getPrefix(RedisAPI.TEMP_STORE_INFO, storeCode);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return FastJsonUtil.jsonToObject(json, Map.class);
		}
		return querySaleInfo(storeCode);
	}

	@Override
	public TemplateStore queryTemplateStoreBySysIdAndStoreCode(String sysId, String storeCode) throws Exception {
		if (CommonUtil.isBlank(sysId) || CommonUtil.isBlank(storeCode)) {
			return null;
		}
		String key = RedisAPI.getPrefix(RedisAPI.STORE_TEMPLATE, sysId, storeCode);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return FastJsonUtil.jsonToObject(json, TemplateStore.class);
		}
		Map<String, Object> params = new HashMap<>(4);
		params.put("sysId", sysId);
		params.put("storeCode", storeCode);
		TemplateStore store = (TemplateStore) queryObjectByParameter(QueryId.QUERY_STORE_BY_PARAM, params);
		if(CommonUtil.isNotBlank(store)) {
			redisUtil.setex(key, RedisAPI.EXPIRE_12_HOUR, FastJsonUtil.objectToString(store));
			return store;
		}
		return null;
	}
	
	@Override
	public TemplateProduct queryTemplateProductBySysIdAndSimpleBarCode(String sysId, String simpleBarCode) {
		String key = RedisAPI.getPrefix(RedisAPI.PRODUCT_TEMPLATE, sysId, simpleBarCode);
		String json = redisUtil.get(key);
		if (CommonUtil.isNotBlank(json)) {
			return FastJsonUtil.jsonToObject(json, TemplateProduct.class);
		}
		Map<String, Object> param = new HashMap<>(2);
		param.put("sysId", sysId);
		param.put("simpleBarCode", simpleBarCode);
		TemplateProduct product = (TemplateProduct) queryObjectByParameter(QueryId.QUERY_PRODUCT_BY_PARAM, param);
		if (product != null) {
			redisUtil.setex(key, RedisAPI.EXPIRE_12_HOUR, FastJsonUtil.objectToString(product));
			return product;
		}
		return null;
	}
	
	@Override
	public String queryBarCodeBySysNameAndSimpleCode(String sysName, String simpleCode) {
		String key = RedisAPI.getPrefix(RedisAPI.SIMPLE_CODE_TEMPLATE, sysName, simpleCode);
		String simpleBarCode = redisUtil.get(key);
		if (CommonUtil.isNotBlank(simpleBarCode)) {
			return simpleBarCode;
		}
		Map<String, Object> param = new HashMap<>(2);
		SimpleCodeEnum simpleCodeEnum = SimpleCodeEnum.getEnum(sysName);
		String column = simpleCodeEnum.getValue();
		param.put("columnName", column);
		param.put("simpleCode", simpleCode);
		SimpleCode code = (SimpleCode)queryObjectByParameter(QueryId.QUERY_SIMPLE_CODE_BY_PARAM, param);
		if (null != code) {
			redisUtil.setex(key, RedisAPI.EXPIRE_12_HOUR, code.getBarCode());
			return code.getBarCode();
		}
		return null;
	}

	@Override
	public List<TemplateStore> queryTemplateStoreList() {
		String key = RedisAPI.getPrefix(RedisAPI.STORE_TEMPLATE);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return (List<TemplateStore>) FastJsonUtil.jsonToList(json, TemplateStore.class);
		}
		List<TemplateStore> list = queryListByObject(QueryId.QUERY_STORE_TEMPLATE, null);
		if(CommonUtil.isNotBlank(list)) {
			redisUtil.setex(key, RedisAPI.EXPIRE_12_HOUR, FastJsonUtil.objectToString(list));
			return list;
		}
		return null;
	}

	@Override
	public List<SimpleCode> queryTemplateSimpleCodeList() {
		String key = RedisAPI.getPrefix(RedisAPI.SIMPLE_CODE_TEMPLATE);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return (List<SimpleCode>) FastJsonUtil.jsonToList(json, SimpleCode.class);
		}
		List<SimpleCode> list = queryListByObject(QueryId.QUERY_STORE_TEMPLATE, null);
		if(CommonUtil.isNotBlank(list)) {
			redisUtil.setex(key, RedisAPI.EXPIRE_12_HOUR, FastJsonUtil.objectToString(list));
			return list;
		}
		return null;
	}

	@Override
	public List<TemplateProduct> queryTemplateProductList() {
		String key = RedisAPI.getPrefix(RedisAPI.PRODUCT_TEMPLATE);
		String json = redisUtil.get(key);
		if(CommonUtil.isNotBlank(json)) {
			return (List<TemplateProduct>) FastJsonUtil.jsonToList(json, TemplateProduct.class);
		}
		List<TemplateProduct> list = queryListByObject(QueryId.QUERY_PRODUCT_TEMPLATE, null);
		if(CommonUtil.isNotBlank(list)) {
			redisUtil.setex(key, RedisAPI.EXPIRE_12_HOUR, FastJsonUtil.objectToString(list));
			return list;
		}
		return null;
	}
	
}
