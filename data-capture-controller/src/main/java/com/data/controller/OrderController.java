package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.Order;
import com.data.dto.CommonDTO;
import com.data.model.OrderModel;
import com.data.service.IOrderService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/order")
@Api(tags = {"订单数据接口"})
@CrossOrigin(origins="*", maxAge=3600)
public class OrderController {
	
	@Autowired
	private IOrderService orderService;
	
	/**
	 * 抓取订单数据
	 * @param queryDate
	 * @param sysId
	 * @param page
	 * @param limit
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/getOrderByWeb", method = RequestMethod.GET)
	@ApiOperation(value = "抓取订单数据", httpMethod = "GET")
	public String getOrderByWeb(String queryDate, Integer limit,
			@RequestParam(value = "id", required = true)Integer id) throws Exception {
		ResultUtil result = orderService.getOrderByWeb(queryDate, id, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 批量抓取
	 * @param queryDate
	 * @param limit
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getOrderByIds", method = RequestMethod.GET)
	@ApiOperation(value = "批量抓取订单数据", httpMethod = "GET")
	public String getOrderByIds(String queryDate, String ids) throws Exception {
		ResultUtil result = orderService.getOrderByIds(queryDate, ids);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 分页多条件查询
	 * @param order
	 * @param common
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getOrderByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "分页多条件查询", httpMethod = "GET")
	public String getOrderByCondition(Order order, CommonDTO common, Integer page, Integer limit) throws Exception {
		ResultUtil result = orderService.getOrderByCondition(common, order, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 选择字段导出订单数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/exportOrderExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出订单数据表", httpMethod = "GET")
	public void expertOrderExcel(@RequestParam(value = "sysId", required = true)String sysId, 
			@RequestParam(value = "stockNameStr", required = true)String stockNameStr, 
			CommonDTO common, Order order, HttpServletResponse response) throws Exception {
		String fileName = "订单处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		orderService.exportOrderExcel(stockNameStr, common, order, output);
	}
	//发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
        	response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 查询订单警报列表
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/queryOrderAlarmList", method = RequestMethod.GET)
    @ApiOperation(value = "查询订单警报列表", httpMethod = "GET")
    public String queryOrderAlarmList(CommonDTO common, OrderModel order, Integer page, Integer limit) throws Exception {
    	ResultUtil result = orderService.queryOrderAlarmList(common, order, page, limit);
    	return FastJsonUtil.objectToString(result);
    }
    
    /**
     * 警报报表输出
     * @param order
     * @param response
     * @throws Exception 
     */
    @RequestMapping(value = "/orderAlarmListExcel", method = RequestMethod.GET)
    @ApiOperation(value = "警报报表输出", httpMethod = "GET")
    public void orderAlarmListExcel(CommonDTO common, OrderModel order, HttpServletResponse response) throws Exception {
    	orderService.orderAlarmListExcel(common, order, response);
    }
    
    /**
     * 订单数据导入
     * @param file
     * @param request
     * @throws Exception 
     */
    @RequestMapping(value = "/uploadOrderExcel", method = RequestMethod.POST)
    @ApiOperation(value = "订单数据导入", httpMethod = "POST")
    public String uploadOrderExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	ResultUtil result = orderService.uploadOrderData(file);
    	return FastJsonUtil.objectToString(result);
    }
}
