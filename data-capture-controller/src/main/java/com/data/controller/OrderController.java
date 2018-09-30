package com.data.controller;

import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

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
import com.data.utils.DateUtil;
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
	public String getOrderByWeb(String queryDate, Integer limit,
			@RequestParam(value = "sysId", required = true)String sysId) {
		ResultUtil result = orderService.getOrderByWeb(queryDate, sysId, limit);
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
	/**
	 * 选择字段导出订单数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 */
	@RequestMapping(value = "/exportOrderExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出订单数据表", httpMethod = "GET")
	public String expertOrderExcel(@RequestParam(value = "sysId", required = true)String sysId, 
			@RequestParam(value = "stockNameStr", required = true)String stockNameStr, 
			CommonDTO common, HttpServletResponse response) {
		String fileName = "订单处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = orderService.exportOrderExcel(sysId, stockNameStr, common, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	//发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
        	response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName + ".xlsx");
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
