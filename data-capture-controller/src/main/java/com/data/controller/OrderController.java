package com.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.service.IOrderService;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dataResolve")
@Api(tags = "订单数据接口")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IOrderService orderService;
	
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
	public ResultUtil queryOrderByCondition(@RequestBody String param) throws Exception{
		ResultUtil result = orderService.queryOrderByCondition(param);
		return result;
	}
}
