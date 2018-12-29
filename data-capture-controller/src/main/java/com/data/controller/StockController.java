package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.Stock;
import com.data.service.IStockService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/stock")
@Api(tags = {"库存数据接口"})
@CrossOrigin(origins="*", maxAge=3600)
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
	public String getStockByWeb(@RequestParam(value = "id", required = true)Integer id, Integer limit) throws Exception {
		ResultUtil result = stockServiceImpl.getStockByWeb(id, limit);
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
	 * 选择字段导出库存数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "exportStockExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出库存数据表", httpMethod = "GET")
	public void exportStockExcel(Stock stock, @RequestParam(value = "stockNameStr", required = true)String stockNameStr, HttpServletResponse response) throws Exception {
		String fileName = "库存处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportStockExcel(stock, stockNameStr, output);
	}
	
	/**
	 * 缺货日报表-导出门店单品表
	 * @param 
	 * @param storeCode 门店编号
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportStoreProductExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出缺货报表-导出门店单品表", httpMethod = "GET")
	public void exportStoreProductExcel(String storeName, HttpServletResponse response) throws IOException {
		String fileName = "缺货报表-门店单品表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportStoreProductExcel(storeName, output);
	}
	
	/**
	 * 缺货日报表-导出系统门店表
	 * @param 
	 * @param sysId
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportSysStoreExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出缺货报表-系统门店表", httpMethod = "GET")
	public void exportSysStoreExcel(String sysId, HttpServletResponse response) throws IOException {
		String fileName = "缺货报表-系统门店表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportSysStoreExcel(sysId, output);
	}
	
	/**
	 * 缺货日报表-导出区域门店表
	 * @param 
	 * @param storeName
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportRegionStoreExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出缺货报表-区域门店表", httpMethod = "GET")
	public void exportRegionStoreExcel(String region, HttpServletResponse response) throws IOException {
		
		String fileName = "缺货报表-区域门店表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportRegionStoreExcel(region, output);
	}
	
	/**
	 * 缺货日报表-导出公司一级表
	 * @param 
	 * @param storeName
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "exportMissFirstComExcel", method = RequestMethod.GET)
	@ApiOperation(value = "导出缺货报表-公司一级表", httpMethod = "GET")
	public void exportMissFirstComExcel(HttpServletResponse response) throws Exception {
		
		String fileName = "缺货报表-公司一级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportMissFirstComExcel(output);
	}
	
	/**
	 * 按区域导出公司一级表
	 * @param  查询时间
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportCompanyExcelByRegion", method = RequestMethod.GET)
	@ApiOperation(value = "按区域导出公司一级表", httpMethod = "GET")
	public void exportCompanyExcelByRegion(HttpServletResponse response) throws IOException {
		String fileName = "库存-按区域-公司一级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportCompanyExcelByRegion(output);
	}
	/**
	 * 按区域导出区域一级表
	 * @param  查询时间
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportRegionExcelByRegion", method = RequestMethod.GET)
	@ApiOperation(value = "按区域导出区域表一级表", httpMethod = "GET")
	public void exportCompanyExcelByRegion(String region, HttpServletResponse response) throws IOException {
		String fileName = "库存-按区域-区域表一级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportRegionExcelByRegion(region, output);
	}
	/**
	 * 按区域导出区域二级表
	 * @param  查询时间
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportProvinceAreaExcelByRegion", method = RequestMethod.GET)
	@ApiOperation(value = "按区域导出区域表二级表", httpMethod = "GET")
	public void exportProvinceAreaExcelByRegion(String region, HttpServletResponse response) throws IOException {
		String fileName = "库存-按区域-区域表二级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportProvinceAreaExcelByRegion(region, output);
	}
	/**
	 * 按区域导出区域三级表
	 * @param  查询时间
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportStoreExcelByProvinceArea", method = RequestMethod.GET)
	@ApiOperation(value = "按区域导出区域表三级表", httpMethod = "GET")
	public void exportStoreExcelByProvinceArea(String provinceArea, HttpServletResponse response) throws IOException {
		String fileName = "库存-按区域-区域表三级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportStoreExcelByRegion(provinceArea, output);
	}
	/**
	 * 按系统导出公司一级表
	 * @param  查询时间
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportCompanyExcelBySys", method = RequestMethod.GET)
	@ApiOperation(value = "按系统导出公司一级表", httpMethod = "GET")
	public void exportCompanyExcelBySys(HttpServletResponse response) throws IOException {
		String fileName = "库存-按系统-公司一级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportCompanyExcelBySys(output);
	}
	/**
	 * 按系统导出区域表一级表
	 * @param  查询时间
	 * @param sysId 系统编号
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "exportRegionExcelBySys", method = RequestMethod.GET)
	@ApiOperation(value = "按系统导出区域表一级表", httpMethod = "GET")
	public void exportRegionExcelBySys(String sysId, HttpServletResponse response) throws Exception {
		String fileName = "库存-按系统-区域表一级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportRegionExcelBySys(sysId, output);
	}
	/**
	 * 按系统导出区域表二级表
	 * @param  查询时间
	 * @param sysId 系统编号
	 * @param region 区域
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "exportRegionSecondExcelBySys", method = RequestMethod.GET)
	@ApiOperation(value = "按系统导出区域表二级表", httpMethod = "GET")
	public void exportRegionSecondExcelBySys(String sysId, String region, HttpServletResponse response) throws IOException {
		String fileName = "库存-按系统-区域表二级表" + DateUtil.getCurrentDateStr();
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		stockServiceImpl.exportRegionSecondExcelBySys(sysId, region, output);
	}
 
	//发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
        	response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 库存数据导入
     * @param file
     * @param request
     * @throws Exception 
     */
    @RequestMapping(value = "/uploadStockExcel", method = RequestMethod.POST)
    @ApiOperation(value = "库存数据导入", httpMethod = "POST")
    public String uploadStockExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	ResultUtil result = stockServiceImpl.uploadStockData(file);
    	return FastJsonUtil.objectToString(result);
    }
}
