package com.data.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.data.bean.PromotionStoreList;
import com.data.bean.TemplateStore;
import com.data.constant.enums.CodeEnum;
import com.data.service.IPromotionStoreListService;
import com.data.service.IRedisService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/promotionStoreList")
@Api(tags = {"促销生效门店接口"})
public class PromotionStoreListController {
	
	@Autowired
	private IPromotionStoreListService promotionStoreListService;
	
	@Autowired
	private IRedisService redisService;
	
	@RequestMapping(value = "savePromotionStoreList", method = RequestMethod.POST)
	@ApiOperation(value = "保存促销生效门店", httpMethod = "POST")
	public String savePromotionStoreList(String param) {
		List<PromotionStoreList> storeList = JSON.parseArray(param, PromotionStoreList.class);
		ResultUtil result = promotionStoreListService.savePromotionStoreList(storeList);
		return FastJsonUtil.objectToString(result);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getPromotionStoreList", method = RequestMethod.GET)
	@ApiOperation(value = "获取促销生效门店", httpMethod = "GET")
	public String getPromotionStoreList(Integer promotionDetailId, String sysId) {
		if (StringUtils.isBlank(sysId)) {
			return FastJsonUtil.objectToString(ResultUtil.error("系统编号不能为空"));
		}
		ResultUtil result = promotionStoreListService.getPromotionStoreList(promotionDetailId);
		
		if (CodeEnum.RESPONSE_00_CODE.value().equals(result.getCode())) {
			List<PromotionStoreList> promotionStoreList = (List<PromotionStoreList>) result.getData();
			List<TemplateStore> templateStoreList = redisService.queryTemplateStoreList();
			
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < promotionStoreList.size(); i++) {
				PromotionStoreList promotionStore = promotionStoreList.get(i);
				String storeCode = promotionStore.getStoreCode();
				
				List<TemplateStore> tempList = (List<TemplateStore>) JSONPath.eval(templateStoreList, "$[sysId='"+ sysId +"']");
				tempList = (List<TemplateStore>) JSONPath.eval(tempList, "$[storeCode='"+ storeCode +"']");
				
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("storeName", JSONPath.eval(tempList, "$[0].standardStoreName"));
				jsonObj.put("storeCode", storeCode);
				jsonArray.add(jsonObj);
			}
			
			return FastJsonUtil.objectToString(ResultUtil.success(jsonArray));
		}
		return FastJsonUtil.objectToString(result);
	}
}
