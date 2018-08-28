package com.data.service;

import com.data.bean.Sale;
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
	
	/**
	 * 查询集合列表
	 * 主要可以拿来做导出用
	 * @param sale
	 * @return
	 */
	ResultUtil querySaleList(Sale sale);
}
