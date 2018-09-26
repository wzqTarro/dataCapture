package com.data.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.data.bean.SimpleCode;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.SimpleCodeEnum;
import com.data.service.IRedisService;
import com.data.service.impl.CommonServiceImpl;

@Component
public class TemplateDataUtil extends CommonServiceImpl {
	
	@Autowired
	private IRedisService redisService;
	
	/**
	 * 获取标准门店信息
	 * @param sysName
	 * @param storeCode
	 * @return
	 * @throws Exception 
	 */
	public TemplateStore getStandardStoreMessage(String sysId, String storeCode) throws Exception {
		if (CommonUtil.isBlank(sysId) || CommonUtil.isBlank(storeCode)) {
			return null;
		}
		return redisService.queryTemplateStoreBySysIdAndStoreCode(sysId, storeCode);
	}
	/**
	 * 获取标准条码信息
	 * @param sysName
	 * @param simpleCode
	 * @return
	 */
	public String getBarCodeMessage(String sysName, String simpleCode) {
		if (CommonUtil.isBlank(sysName) || CommonUtil.isBlank(simpleCode)) {
			return null;
		}
		Map<String, Object> param = new HashMap<>(2);
		SimpleCodeEnum simpleCodeEnum = SimpleCodeEnum.getEnum(sysName);
		String column = simpleCodeEnum.getValue();
		param.put("columnName", column);
		param.put("simpleCode", simpleCode);
		SimpleCode code = (SimpleCode)queryObjectByParameter(QueryId.QUERY_SIMPLE_CODE_BY_PARAM, param);
		return code.getBarCode();
	}
	/**
	 * 获取标准商品信息
	 * @param sysName
	 * @param simpleCode
	 * @return
	 */
	public TemplateProduct getStandardProductMessage(String sysId, String simpleBarCode) {
		if (CommonUtil.isBlank(sysId)) {
			return null;
		}
		Map<String, Object> param = new HashMap<>(2);
		param.put("sysId", sysId);
		param.put("simpleBarCode", simpleBarCode);
		List<TemplateProduct> product = queryListByObject(QueryId.QUERY_PRODUCT_BY_PARAM, param);
		return CommonUtil.isBlank(product) ? null :product.get(0);
	}
}
