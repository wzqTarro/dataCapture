package com.data.service;

import java.io.IOException;
import java.io.OutputStream;

import com.data.bean.Stock;
import com.data.utils.ResultUtil;

public interface IStockService {

	/**
	 * 抓取库存数据
	 * @param common
	 * @return
	 */
	ResultUtil getStockByWeb(String sysId, Integer limit) throws Exception;
	/**
	 * 多条件查询库存
	 * @param common
	 * @param stock
	 * @return
	 */
	ResultUtil getStockByParam(Stock stock, Integer page, Integer limit) throws Exception ;
	/**
	 * 缺货日报表-导出门店单品表
	 * @param queryDate 查询时间
	 * @param storeName 门店名称
	 * @return
	 */
	void exportStoreProductExcel(String storeCode, OutputStream output) throws IOException;
	/**
	 * 缺货日报表-导出系统门店表
	 * @param queryDate
	 * @param sysName
	 * @return
	 */
	void exportSysStoreExcel(String sysId, OutputStream output) throws IOException;
	/**
	 * 缺货日报表-导出区域门店表
	 * @param queryDate
	 * @param region
	 * @return
	 */
	void exportRegionStoreExcel(String region, OutputStream output) throws IOException;
	/**
	 * 缺货日报表-导出公司一级表
	 * @param queryDate
	 * @param region
	 * @return
	 */
	void exportMissFirstComExcel(OutputStream output) throws IOException;
	/**
	 * 自定义字段导出库存数据表
	 * @param stock
	 * @param common
	 * @return
	 */
	void exportStockExcel(Stock stock, String stockNameStr, OutputStream output) throws Exception;
	/**
	 * 按区域导出公司一级表
	 * @param queryDate
	 * @return
	 */
	void exportCompanyExcelByRegion(OutputStream output) throws IOException;
	/**
	 * 按区域导出区域表一级表
	 * @param queryDate
	 * @return
	 */
	void exportRegionExcelByRegion(String region, OutputStream output) throws IOException;
	/**
	 * 按区域导出区域表二级表
	 * @param queryDate
	 * @return
	 */
	void exportProvinceAreaExcelByRegion(String region, OutputStream output) throws IOException;
	/**
	 * 按区域导出区域表三级表
	 * @param queryDate
	 * @return
	 */
	void exportStoreExcelByRegion(String provinceArea, OutputStream output) throws IOException;
	/**
	 * 按系统导出公司一级表
	 * @param queryDate
	 * @return
	 */
	void exportCompanyExcelBySys(OutputStream output) throws IOException;
	/**
	 * 按系统导出区域表一级表
	 * @param queryDate
	 * @return
	 */
	ResultUtil exportRegionExcelBySys(String sysId, OutputStream output) throws IOException;
	/**
	 * 按系统导出区域表二级表
	 * @param queryDate
	 * @return
	 */
	ResultUtil exportRegionSecondExcelBySys(String sysId, String region, OutputStream output) throws IOException;
}
