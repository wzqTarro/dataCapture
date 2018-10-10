package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.PromotionDetail;
import com.data.dto.CommonDTO;
import com.data.service.IPromotionDetail;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/promotionDetail")
@Api(tags = {"促销明细接口"})
public class PromotionDetailController {

	@Autowired
	private IPromotionDetail promotionDetailService;
	
	@RequestMapping(value = "/getPromotionDetailByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "多條件分頁查詢", httpMethod = "GET")
	public String getPromotionDetailByCondition(CommonDTO common, String sysId, String productCode, Integer page,
			Integer limit) throws Exception {
		ResultUtil result = promotionDetailService.getPromotionDetailByCondition(common, sysId, productCode, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	
	@RequestMapping(value = "/insertPromotionDetail", method = RequestMethod.POST)
	@ApiOperation(value = "插入", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "supplyPriceStartTime", value = "促销供价生效日期", paramType = "query"),
		@ApiImplicitParam(name = "supplyPriceEndTime", value = "促销供价结束日期", paramType = "query"),
		@ApiImplicitParam(name = "sellPriceStartTime", value = "促销售价生效日期", paramType = "query"),
		@ApiImplicitParam(name = "sellPriceEndTime", value = "促销售价结束日期", paramType = "query")
	})	
	public String insertPromotionDetail(PromotionDetail promotionDetail, String supplyPriceStartTime, String supplyPriceEndTime, 
			String sellPriceStartTime, String sellPriceEndTime) {
		ResultUtil result = promotionDetailService.insertPromotionDetail(promotionDetail, supplyPriceStartTime, supplyPriceEndTime, sellPriceStartTime, sellPriceEndTime);
		return FastJsonUtil.objectToString(result);
	}
	
	@RequestMapping(value = "/updatePromotionDetail", method = RequestMethod.PUT)
	@ApiOperation(value = "更新", httpMethod = "PUT")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "supplyPriceStartTime", value = "促销供价生效日期", paramType = "query"),
		@ApiImplicitParam(name = "supplyPriceEndTime", value = "促销供价结束日期", paramType = "query"),
		@ApiImplicitParam(name = "sellPriceStartTime", value = "促销售价生效日期", paramType = "query"),
		@ApiImplicitParam(name = "sellPriceEndTime", value = "促销售价结束日期", paramType = "query")
	})	
	public String updatePromotionDetail(PromotionDetail promotionDetail, String supplyPriceStartTime, String supplyPriceEndTime, 
			String sellPriceStartTime, String sellPriceEndTime) {
		ResultUtil result = promotionDetailService.updatePromotionDetail(promotionDetail, supplyPriceStartTime, supplyPriceEndTime, sellPriceStartTime, sellPriceEndTime);
		return FastJsonUtil.objectToString(result);
	}
	
	@RequestMapping(value = "/deletePromotionDetail", method = RequestMethod.DELETE)
	@ApiOperation(value = "刪除", httpMethod = "DELETE")
	public String deletePromotionDetail(int id) {
		ResultUtil result = promotionDetailService.deletePromotionDetail(id);
		return FastJsonUtil.objectToString(result);
	}
}
