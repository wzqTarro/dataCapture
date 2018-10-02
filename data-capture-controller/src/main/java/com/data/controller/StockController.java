package com.data.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.buf.UEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Stock;
import com.data.dto.CommonDTO;
import com.data.service.IStockService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/stock")
@Api(tags = {"库存数据接口"})
//@CrossOrigin(origins="*", maxAge=3600)
public class StockController {
	
	@Autowired
	private IStockService stockServiceImpl;

	/**
	 * 抓取库存数据
	 * @param common
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "getStockByWeb", method = RequestMethod.GET)
	@ApiOperation(value = "抓取库存数据", httpMethod = "GET")
	public String getStockByWeb(@RequestParam(value = "sysId", required = true)String sysId, Integer limit) throws Exception {
		ResultUtil result = stockServiceImpl.getStockByWeb(sysId, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 多条件查询
	 * @param common
	 * @param stock
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "getStockByParam", method = RequestMethod.GET)
	@ApiOperation(value = "多条件查询库存数据", httpMethod = "GET")
	public String getStockByParam(Stock stock, Integer page, Integer limit) throws Exception {
		ResultUtil result = stockServiceImpl.getStockByParam(stock, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 缺货日报表-导出门店单品表
	 * @param 
	 * @param storeCode 门店编号
	 * @return
	 */
	@RequestMapping(value = "exportStoreProductExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出门店单品表", httpMethod = "GET")
	public void exportStoreProductExcel(String storeCode, HttpServletResponse response) {
		String fileName = "缺货表报-门店单品表";
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			stockServiceImpl.exportStoreProductExcel(storeCode, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 缺货日报表-导出系统门店表
	 * @param 
	 * @param sysId
	 * @return
	 */
	@RequestMapping(value = "exportSysStoreExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出缺货报表-系统门店表", httpMethod = "GET")
	public void exportSysStoreExcel(String sysId, HttpServletResponse response) {
		String fileName = "缺货表报-系统门店表";
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			stockServiceImpl.exportSysStoreExcel(sysId, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 缺货日报表-导出区域门店表
	 * @param 
	 * @param storeName
	 * @return
	 */
	@RequestMapping(value = "exportRegionStoreExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出缺货报表-区域门店表", httpMethod = "GET")
	public void exportRegionStoreExcel(String region, HttpServletResponse response) {
		
		String fileName = "缺货表报-区域门店表";
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			stockServiceImpl.exportRegionStoreExcel(region, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 缺货日报表-导出公司一级表
	 * @param 
	 * @param storeName
	 * @return
	 */
	@RequestMapping(value = "exportMissFirstComExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出缺货报表-区域门店表", httpMethod = "GET")
	public void exportMissFirstComExcel(HttpServletResponse response) {
		
		String fileName = "缺货表报-区域门店表" ;
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			stockServiceImpl.exportMissFirstComExcel(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 选择字段导出库存数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 */
	@RequestMapping(value = "exportStockExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出库存数据表", httpMethod = "GET")
	public void exportStockExcel(@RequestParam(value = "sysId", required = true)String sysId, 
			@RequestParam(value = "stockNameStr", required = true)String stockNameStr, HttpServletResponse response) {
		String fileName = "库存处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			stockServiceImpl.exportStockExcel(sysId, stockNameStr, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 按区域导出公司一级表
	 * @param  查询时间
	 * @return
	 */
	@RequestMapping(value = "exportCompanyExcelByRegion", method = RequestMethod.GET)
	@ApiOperation(value = "按区域导出公司一级表", httpMethod = "GET")
	public void exportCompanyExcelByRegion(HttpServletResponse response) {
		String fileName = "库存-按区域公司一级表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			stockServiceImpl.exportCompanyExcelByRegion(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 按系统导出公司一级表
	 * @param  查询时间
	 * @return
	 */
	@RequestMapping(value = "exportCompanyExcelBySys", method = RequestMethod.GET)
	@ApiOperation(value = "按系统导出公司一级表", httpMethod = "GET")
	public void exportCompanyExcelBySys(HttpServletResponse response) {
		String fileName = "库存-按系统公司一级表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			stockServiceImpl.exportCompanyExcelBySys(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 按系统导出区域表一级表
	 * @param  查询时间
	 * @param sysId 系统编号
	 * @return
	 */
	@RequestMapping(value = "exportRegionExcelBySys", method = RequestMethod.GET)
	@ApiOperation(value = "按系统导出区域表一级表", httpMethod = "GET")
	public String exportRegionExcelBySys(String sysId, HttpServletResponse response) {
		String fileName = "库存-按系统区域表一级表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.exportRegionExcelBySys(sysId, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 按系统导出区域表二级表
	 * @param  查询时间
	 * @param sysId 系统编号
	 * @param region 区域
	 * @return
	 */
	@RequestMapping(value = "exportRegionSecondExcelBySys", method = RequestMethod.GET)
	@ApiOperation(value = "按系统导出区域表二级表", httpMethod = "GET")
	public String exportRegionSecondExcelBySys(String sysId, String region, HttpServletResponse response) {
		String fileName = "库存-按系统区域表二级表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.exportRegionSecondExcelBySys(sysId, region, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
 
	//发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
        	response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName + ".xlsx");
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
