package com.data.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.dto.CommonDTO;
import com.data.service.IStockService;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/stock")
@Api(tags = {"库存数据接口"})
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
	public ResultUtil getStockByWeb(@RequestParam(required = false) CommonDTO common) throws IOException {
		ResultUtil result = stockServiceImpl.getStockByWeb(common);
		return result;
	}
}
