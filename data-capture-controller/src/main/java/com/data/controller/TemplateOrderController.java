package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.service.ITemplateOrderService;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dataResolve")
@Api(tags = "订单数据接口")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TemplateOrderController {
	
	@Autowired
	private ITemplateOrderService orderService;
	
	/**
	 * 查询抓取数据分页显示
	 * @param queryDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "queryOrderByCondition", method = RequestMethod.POST)
	@ApiOperation(value = "分页查询抓取的数据", httpMethod = "POST")
	public ResultUtil queryOrderByCondition(@RequestParam("param") String param) throws Exception{
		ResultUtil result = orderService.queryOrderByCondition(param);
		return result;
	}
}
