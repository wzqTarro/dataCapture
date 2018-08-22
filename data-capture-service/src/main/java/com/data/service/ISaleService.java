package com.data.service;

import com.data.utils.ResultUtil;

public interface ISaleService {
	/**
	 * python抓取销售数据
	 * @param param
	 * @return
	 */
	ResultUtil getSaleByWeb(String param);
	/**
	 * 多条件查询销售数据
	 * @param param
	 * @return
	 */
	ResultUtil getSaleByParam(String param) throws Exception;
}
