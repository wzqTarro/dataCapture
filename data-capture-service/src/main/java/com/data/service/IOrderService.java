package com.data.service;

import com.data.utils.ResultUtil;

public interface IOrderService {
	/**
	 * 分页查询
	 * @param queryDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ResultUtil queryOrderByCondition(String param) throws Exception ;

}
