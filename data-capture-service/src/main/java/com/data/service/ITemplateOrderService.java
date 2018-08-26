package com.data.service;

import com.data.utils.ResultUtil;

public interface ITemplateOrderService {
	/**
	 * 分页查询
	 * @param queryDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ResultUtil queryOrderByCondition(String param) throws Exception ;

}
