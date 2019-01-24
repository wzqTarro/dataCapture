package com.data.utils;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONPath;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.dbSql.UpdateId;
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
	 * @throws Exception 
	 */
	public String getBarCodeMessage(String simpleBarCode, String sysName, String simpleCode){
		if (CommonUtil.isBlank(simpleBarCode)) {
			if (CommonUtil.isBlank(sysName) || CommonUtil.isBlank(simpleCode)) {
				return null;
			}
			return redisService.queryBarCodeBySysNameAndSimpleCode(sysName, simpleCode);
		} else {
			return simpleBarCode;
		}
		
	}
	/**
	 * 获取标准商品信息
	 * @param sysName
	 * @param simpleCode
	 * @return
	 */
	public TemplateProduct getStandardProductMessage(String sysId, String simpleBarCode) {
		if (CommonUtil.isBlank(sysId) || CommonUtil.isBlank(simpleBarCode)) {
			return null;
		}
		return redisService.queryTemplateProductBySysIdAndSimpleBarCode(sysId, simpleBarCode);
	}
}
