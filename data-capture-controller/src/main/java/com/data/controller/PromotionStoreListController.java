package com.data.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.data.bean.PromotionStoreList;
import com.data.service.IPromotionStoreListService;
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
	
	@RequestMapping(value = "savePromotionStoreList", method = RequestMethod.POST)
	@ApiOperation(value = "保存促销生效门店", httpMethod = "POST")
	public String savePromotionStoreList(String param) {
		List<PromotionStoreList> storeList = JSON.parseArray(param, PromotionStoreList.class);
		ResultUtil result = promotionStoreListService.savePromotionStoreList(storeList);
		return FastJsonUtil.objectToString(result);
	}
}
