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
	ResultUtil getStockByWeb(CommonDTO common, int sysId, Integer page, Integer limit) throws IOException ;
	/**
	 * 多条件查询库存
	 * @param common
	 * @param stock
	 * @return
	 */
	ResultUtil getStockByParam(CommonDTO common, Stock stock, Integer page, Integer limit) throws Exception ;
	/**
	 * 生成门店单品表
	 * @param queryDate 查询时间
	 * @param storeName 门店名称
	 * @return
	 */
	ResultUtil createStoreProductExcel(String queryDate, String storeName);
	/**
	 * 生成系统门店表
	 * @param queryDate
	 * @param sysName
	 * @return
	 */
	ResultUtil createSysStoreExcel(String queryDate, String sysName);
	/**
	 * 生成区域门店表
	 * @param queryDate
	 * @param region
	 * @return
	 */
	ResultUtil createRegionStoreExcel(String queryDate, String region);
}
