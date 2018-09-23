package com.data.service;

import java.io.IOException;
import java.io.OutputStream;

import com.data.bean.Stock;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface IStockService {

	/**
	 * 抓取库存数据
	 * @param common
	 * @return
	 */
	ResultUtil getStockByWeb(CommonDTO common, String sysId, Integer page, Integer limit) throws IOException ;
	/**
	 * 多条件查询库存
	 * @param common
	 * @param stock
	 * @return
	 */
	ResultUtil getStockByParam(CommonDTO common, Stock stock, Integer page, Integer limit) throws Exception ;
	/**
	 * 导出门店单品表
	 * @param queryDate 查询时间
	 * @param storeName 门店名称
	 * @return
	 */
	ResultUtil expertStoreProductExcel(String queryDate, String storeCode, OutputStream output) throws IOException;
	/**
	 * 导出系统门店表
	 * @param queryDate
	 * @param sysName
	 * @return
	 */
	ResultUtil expertSysStoreExcel(String queryDate, String sysId, OutputStream output) throws IOException;
	/**
	 * 导出区域门店表
	 * @param queryDate
	 * @param region
	 * @return
	 */
	ResultUtil expertRegionStoreExcel(String queryDate, String region, OutputStream output) throws IOException;
	/**
	 * 自定义字段导出库存数据表
	 * @param stock
	 * @param common
	 * @return
	 */
	ResultUtil expertStockExcel(String stockNameStr, CommonDTO common, OutputStream output) throws Exception;
	/**
	 * 按系统导出公司一级表
	 * @param queryDate
	 * @return
	 */
	ResultUtil expertCompanyExcelBySys(String queryDate, OutputStream output) throws IOException;
	/**
	 * 按系统导出区域表一级表
	 * @param queryDate
	 * @return
	 */
	ResultUtil expertRegionExcelBySys(String queryDate, String sysId, OutputStream output) throws IOException;
	/**
	 * 按系统导出区域表二级表
	 * @param queryDate
	 * @return
	 */
	ResultUtil expertRegionSecondExcelBySys(String queryDate, String sysId, String region, OutputStream output) throws IOException;
}
