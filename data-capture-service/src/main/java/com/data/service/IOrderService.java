package com.data.service;

import java.io.OutputStream;

import com.data.bean.Order;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface IOrderService {
	/**
	 * python抓取数据
	 * @param queryDate
	 * @param page
	 * @param limit
	 * @return
	 */
	ResultUtil getOrderByWeb(String queryDate, String sysId, Integer limit);
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
	ResultUtil exportOrderExcel(String sysId, String stockNameStr, CommonDTO common, OutputStream output) throws Exception;
}
