package com.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Order;
import com.data.dto.CommonDTO;
import com.data.service.IOrderService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/order")
@Api(tags = {"订单数据接口"})
//@CrossOrigin(origins="*", maxAge=3600)
public class OrderController {
	
	private static Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private IOrderService orderService;
	
	/**
	 * 抓取订单数据
	 * @param queryDate
	 * @param sysId
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/getOrderByWeb", method = RequestMethod.GET)
	@ApiOperation(value = "抓取订单数据", httpMethod = "GET")
	public String getOrderByWeb(String queryDate, String sysId, Integer page, Integer limit) {
		ResultUtil result = orderService.getOrderByWeb(queryDate, sysId, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 分页多条件查询
	 * @param order
	 * @param common
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/getOrderByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "分页多条件查询", httpMethod = "GET")
	public String getOrderByCondition(Order order, CommonDTO common, Integer page, Integer limit) {
		ResultUtil result = ResultUtil.error();
		try {
			result = orderService.getOrderByCondition(common, order, page, limit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}

}
