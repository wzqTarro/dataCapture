package com.data.service;

import java.io.IOException;

import com.data.bean.Stock;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface IStockService {

	/**
	 * 抓取库存数据
	 * @param common
	 * @return
	 */
	ResultUtil getStockByWeb(CommonDTO common) throws IOException ;
	/**
	 * 多条件查询库存
	 * @param common
	 * @param stock
	 * @return
	 */
	ResultUtil getStockByParam(CommonDTO common, Stock stock) throws Exception ;
}
