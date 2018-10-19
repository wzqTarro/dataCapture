package com.data.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.data.bean.Order;
import com.data.dto.CommonDTO;
import com.data.model.OrderModel;
import com.data.utils.ResultUtil;

public interface IOrderService {
	/**
	 * python抓取数据
	 * @param queryDate
	 * @param page
	 * @param limit
	 * @return
	 */
	ResultUtil getOrderByWeb(String queryDate, String sysId, Integer limit) throws IOException ;
	/**
	 * 分页查询
	 * @param queryDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ResultUtil getOrderByCondition(CommonDTO common, Order order, Integer page, Integer limit) throws Exception ;
	/**
	 * 选择字段导出Excel
	 * @param stockNameStr
	 * @param common
	 * @param output
	 * @return
	 * @throws Exception
	 */
	void exportOrderExcel(String stockNameStr, CommonDTO common, Order order, OutputStream output) throws Exception;
	
	/**
	 * 查询订单警报集合
	 * @param order
	 * @return
	 */
	ResultUtil queryOrderAlarmList(CommonDTO common, OrderModel order, Integer page, Integer limit) throws Exception;
	
	/**
	 * 报警订单报表输出
	 * @param order
	 * @param response
	 */
	void orderAlarmListExcel(CommonDTO common, OrderModel order, HttpServletResponse response) throws Exception;
}
