package com.data.service;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.data.bean.Sale;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface ISaleService {
	/**
	 * python抓取销售数据
	 * @param para
	 * @return
	 */
	ResultUtil getSaleByWeb(String queryDate, Integer id, Integer limit) throws Exception;
	/**
	 * 多条件查询销售数据
	 * @param param
	 * @return
	 */
	ResultUtil getSaleByParam(CommonDTO common, Sale sale, Integer page, Integer limit) throws Exception;
	
	/**
	 * 查询集合列表
	 * 主要可以拿来做导出用
	 * @param sale
	 * @return
	 */
	ResultUtil querySaleList(Sale sale);
	
	/**
	 * 数据导出
	 * @param response
	 */
	ResultUtil storeDailyexcel(String system, String region, String province, String store, HttpServletResponse response) throws Exception;

	/**
	 * 选择字段导出Excel
	 * @param stockNameStr
	 * @param common
	 * @param output
	 * @return
	 * @throws Exception
	 */
	void exportSaleExcel(String stockNameStr, CommonDTO common, Sale sale, OutputStream output) throws Exception;
	
	/**
	 * 手动计算补全当天之前的门店销售数据
	 * @return
	 */
	ResultUtil calculateStoreDailySale() throws Exception;
	
	/**
	 * 按条件查询门店日报表销售数据
	 * @param system
	 * @param region
	 * @param province
	 * @param store
	 * @return
	 */
	ResultUtil queryStoreDailySaleReport(String system, String region, String province, String store, Integer page, Integer limit) throws Exception;
	
	/**
	 * 按区域公司一级表
	 */
	void exportSaleExcelByRegion(String saleDate, HttpServletResponse response) throws Exception;
	
	/**
	 * 按区域 系统一级表
	 * @param sysId
	 * @param saleDate
	 * @param response
	 * @throws Exception
	 */
	void exportSaleExcelBySysId(String region, String saleDate, HttpServletResponse response) throws Exception;
	
	/**
	 * 按区域 省区一级表
	 * @param region
	 * @param saleDate
	 * @param response
	 * @throws Exception
	 */
	void exportSaleExcelByProvinceArea(String region, String saleDate, HttpServletResponse response) throws Exception;
	
	/**
	 * 按区域 门店一级表
	 * @param storeCode
	 * @param saleDate
	 * @param response
	 * @throws Exception
	 */
	void exportSaleExcelByStoreCode(String provinceArea, String saleDate, HttpServletResponse response) throws Exception;
	
	
	/**
	 * 按系统导出公司一级表
	 */
	void exportCompanyExcelBySys(String queryDate, OutputStream output) throws Exception;
	/**
	 * 按系统导出区域表一级表
	 */
	void exportRegionFirstExcelBySys(String queryDate, String sysId, OutputStream output) throws Exception;
	/**
	 * 按系统导出区域表二级表
	 */
	void exportRegionSecondExcelBySys(String queryDate, String sysId, String region, OutputStream output) throws Exception;
	
	/**
	 * 销售数据导入
	 * @param file
	 * @return
	 * @throws Exception
	 */
	ResultUtil uploadSaleData(MultipartFile file) throws Exception;

	/**
	 * 定时任务将数据放入缓存
	 */
	void scheduleSaleJob() throws Exception;
}
