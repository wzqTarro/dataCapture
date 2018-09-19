package com.data.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.service.ISaleService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/sale")
// @Api(tags = "销售数据接口")
public class SaleController {

	@Autowired
	private ISaleService saleService;
	
	/**
	 * python抓取销售数据
	 * @param param
	 * @return
	 * @throws Exception 
	 * @throws IOException, DataException 
	 */
	@RequestMapping(value = "/getDataByWeb", method = RequestMethod.POST)
	@ApiOperation(value = "python抓取销售数据", httpMethod = "POST")
	public String getDataByWeb(@RequestParam(value = "common", required = false)CommonDTO commonDTO, 
			@RequestParam(value = "sysId")int sysId,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception{
		ResultUtil result = saleService.getSaleByWeb(commonDTO, sysId, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 多条件分页查询销售数据
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getDataByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件分页查询销售数据", httpMethod = "POST")
	public String getDataByParam(@RequestParam(value = "common", required = false)CommonDTO common,
			@RequestParam(value = "sale", required = false)Sale sale,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception {
		ResultUtil result = saleService.getSaleByParam(common, sale, page, limit);
		return FastJsonUtil.objectToString(result);
	}
}
