package com.data.service;

import java.util.List;
import java.util.Map;

import com.data.bean.SimpleCode;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
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
	void setSaleDailyMessageByStore(String dateStr, List<Map<String, Object>> storeDailySaleList) throws Exception;
	
	/**
	 * 查询销售门店数据
	 * @return
	 */
	List<Map<String, Object>> querySaleList() throws Exception;
	
	/**
	 * 根据门店信息查询sale信息
	 * @param storeCode
	 * @return
	 */
	Map<String, Object> querySaleInfo(String storeCode) throws Exception;
	
	/**
	 * 暂存销售信息
	 * @param storeCode
	 * @param saleInfoMap
	 * @throws Exception
	 */
	void setTempSaleInfo(String storeCode, Map<String, Object> saleInfoMap) throws Exception;
	
	/**
	 * 根据门店编号来查缓存
	 * @param storeCode
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> queryTempSaleInfo(String storeCode) throws Exception;
	
	/**
	 * 按照sysId和storeCode
	 * @param sysId
	 * @param storeCode
	 * @return
	 * @throws Exception
	 */
	TemplateStore queryTemplateStoreBySysIdAndStoreCode(String sysId, String storeCode) throws Exception;
	
	/**
	 * 根据sysId和simpleBarCode查询商品模板
	 * @param sysId
	 * @param simpleBarCode
	 * @return
	 */
	TemplateProduct queryTemplateProductBySysIdAndSimpleBarCode(String sysId, String simpleBarCode);
	
	/**
	 * 根据sysName和simpleCode查询条码
	 * @param sysName
	 * @param simpleCode
	 * @return
	 */
	String queryBarCodeBySysNameAndSimpleCode(String sysName, String simpleCode);
	
	/**
	 * 查询门店模板列表
	 * @return
	 */
	List<TemplateStore> queryTemplateStoreList();
	
	/**
	 * 查询条码模板列表
	 * @return
	 */
	List<SimpleCode> queryTemplateSimpleCodeList();
	
	/**
	 * 查询单品模板列表
	 * @return
	 */
	List<TemplateProduct> queryTemplateProductList();

}
