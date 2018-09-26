package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

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
	@RequestMapping(value = "getStockByWeb", method = RequestMethod.POST)
	@ApiOperation(value = "抓取库存数据", httpMethod = "POST")
	@ApiImplicitParam(name = "sysId", value = "系统ID", required = true)
	public String getStockByWeb(String queryDate, String sysId, 
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception {
		ResultUtil result = stockServiceImpl.getStockByWeb(queryDate, sysId, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 多条件查询
	 * @param common
	 * @param stock
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "getStockByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件查询库存数据", httpMethod = "POST")
	public String getStockByParam(@RequestParam(value = "common", required = false)CommonDTO common,
			@RequestParam(value = "stock", required = false)Stock stock,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception {
		ResultUtil result = stockServiceImpl.getStockByParam(common, stock, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 导出门店单品表
	 * @param queryDate
	 * @param storeCode 门店编号
	 * @return
	 */
	@RequestMapping(value = "expertStoreProductExcel", method = RequestMethod.POST)
	@ApiOperation(value = "导出门店单品表", httpMethod = "POST")
	public String expertStoreProductExcel(String queryDate, String storeCode, 
			HttpServletResponse response) {
		String fileName = "缺货表报-门店单品表" + queryDate;
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.expertStoreProductExcel(queryDate, storeCode, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 导出系统门店表
	 * @param queryDate
	 * @param sysId
	 * @return
	 */
	@RequestMapping(value = "expertSysStoreExcel", method = RequestMethod.POST)
	@ApiOperation(value = "导出缺货报表-系统门店表", httpMethod = "POST")
	public String expertSysStoreExcel(String queryDate, String sysId, 
			HttpServletResponse response) {
		String fileName = "缺货表报-系统门店表" + queryDate;
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.expertSysStoreExcel(queryDate, sysId, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 导出区域门店表
	 * @param queryDate
	 * @param storeName
	 * @return
	 */
	@RequestMapping(value = "expertRegionStoreExcel", method = RequestMethod.POST)
	@ApiOperation(value = "导出缺货报表-区域门店表", httpMethod = "POST")
	public String expertRegionStoreExcel(String queryDate, String region, 
			HttpServletResponse response) {
		
		String fileName = "缺货表报-区域门店表" + queryDate;
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.expertRegionStoreExcel(queryDate, region, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 自定义字段导出库存数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 */
	@RequestMapping(value = "expertStockExcel", method = RequestMethod.POST)
	@ApiOperation(value = "自定义字段导出库存数据表", httpMethod = "POST")
	public String expertStockExcel(String stockNameStr, CommonDTO common, HttpServletResponse response) {
		String fileName = "库存处理表" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.expertStockExcel(stockNameStr, common, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 按系统导出公司一级表
	 * @param queryDate 查询时间
	 * @return
	 */
	@RequestMapping(value = "expertCompanyExcelBySys", method = RequestMethod.POST)
	@ApiOperation(value = "按系统导出公司一级表", httpMethod = "POST")
	public String expertCompanyExcelBySys(String queryDate, HttpServletResponse response) {
		String fileName = "库存-按系统公司一级表" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.expertCompanyExcelBySys(queryDate, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 按系统导出区域表一级表
	 * @param queryDate 查询时间
	 * @param sysId 系统编号
	 * @return
	 */
	@RequestMapping(value = "expertRegionExcelBySys", method = RequestMethod.POST)
	@ApiOperation(value = "按系统导出区域表一级表", httpMethod = "POST")
	public String expertRegionExcelBySys(String queryDate, String sysId, HttpServletResponse response) {
		String fileName = "库存-按系统区域表一级表" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.expertRegionExcelBySys(queryDate, sysId, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 按系统导出区域表二级表
	 * @param queryDate 查询时间
	 * @param sysId 系统编号
	 * @param region 区域
	 * @return
	 */
	@RequestMapping(value = "expertRegionSecondExcelBySys", method = RequestMethod.POST)
	@ApiOperation(value = "按系统导出区域表二级表", httpMethod = "POST")
	public String expertRegionSecondExcelBySys(String queryDate, String sysId, String region, HttpServletResponse response) {
		String fileName = "库存-按系统区域表二级表" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		ResultUtil result = ResultUtil.error();
		try {
			output = response.getOutputStream();
			result = stockServiceImpl.expertRegionSecondExcelBySys(queryDate, sysId, region, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
 
	//发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
