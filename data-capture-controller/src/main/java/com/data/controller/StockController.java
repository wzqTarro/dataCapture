package com.data.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Stock;
import com.data.dto.CommonDTO;
import com.data.service.IStockService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/stock")
// @Api(tags = {"库存数据接口"})
public class StockController {
	
	@Autowired
	private IStockService stockServiceImpl;

	/**
	 * 抓取库存数据
	 * @param common
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "getStockByWeb", method = RequestMethod.POST)
	@ApiOperation(value = "抓取库存数据", httpMethod = "POST")
	@ApiImplicitParam(name = "sysId", value = "系统ID", required = true)
	public String getStockByWeb(@RequestParam(required = false) CommonDTO common,
			@RequestParam(value = "sysId")int sysId, 
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws IOException {
		ResultUtil result = stockServiceImpl.getStockByWeb(common, sysId, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 多条件查询
	 * @param common
	 * @param stock
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "getStockByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件查询库存数据", httpMethod = "POST")
	public String getStockByParam(@RequestParam(value = "common", required = false)CommonDTO common,
			@RequestParam(value = "stock", required = false)Stock stock,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception {
		ResultUtil result = stockServiceImpl.getStockByParam(common, stock, page, limit);
		return FastJsonUtil.objectToString(result);
	}
}
