package com.data.service;

import java.io.IOException;

import com.data.bean.Sale;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface ISaleService {
	/**
	 * python抓取销售数据
	 * @param para
	 * @return
	 */
	ResultUtil getSaleByWeb(CommonDTO common) throws Exception ;
	/**
	 * 多条件查询销售数据
	 * @param param
	 * @return
	 */
	ResultUtil getSaleByParam(CommonDTO common, Sale sale) throws Exception;
	
	/**
	 * 查询集合列表
	 * 主要可以拿来做导出用
	 * @param sale
	 * @return
	 */
	ResultUtil querySaleList(Sale sale);
}
