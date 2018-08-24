package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.service.ISaleService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/sale")
@Api(tags = "销售数据接口")
public class SaleController {

	@Autowired
	private ISaleService saleService;
	
	/**
	 * python抓取销售数据
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/getDataByWeb", method = RequestMethod.POST)
	@ApiOperation(value = "/getDataByWeb", httpMethod = "POST")
	public String getDataByWeb(@RequestBody String param) {
		ResultUtil result = saleService.getSaleByWeb(param);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 多条件分页查询销售数据
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getDataByParam", method = RequestMethod.POST)
	@ApiOperation(value = "/getDataByParam", httpMethod = "POST")
	public String getDataByParam(@RequestBody String param) throws Exception {
		ResultUtil result = saleService.getSaleByParam(param);
		return FastJsonUtil.objectToString(result);
	}
}
