package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Order;
import com.data.constant.PageRecord;
import com.data.service.IOrderService;
import com.data.utils.ResultUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dataResolve")
@Api(value = "抓取订单数据")
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
	@RequestMapping(value = "queryOrderByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "分页查询抓取的数据", httpMethod = "GET")
	public ResultUtil queryOrderByCondition(@RequestParam(value="startDate", required=false)String startDate,
			@RequestParam(value = "endDate", required = false)String endDate, String companyCode,
			@RequestParam(value = "page", required=false)String pageNum, @RequestParam(value = "limit", required = false)String pageSize
			) throws Exception{
		logger.info("---------->>>>>>>>>queryOrderByCondition start<<<<<<<<<-------------");
		ResultUtil result = orderService.queryOrderByCondition(startDate, endDate, companyCode, pageNum, pageSize);
		logger.info("---------->>>>>>>>>queryOrderByCondition end<<<<<<<<<--------------");
		return result;
	}
}
