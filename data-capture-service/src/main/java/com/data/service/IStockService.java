package com.data.service;

import java.io.IOException;

import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface IStockService {

	/**
	 * 抓取库存数据
	 * @param common
	 * @return
	 */
	ResultUtil getStockByWeb(CommonDTO common) throws IOException ;
}
