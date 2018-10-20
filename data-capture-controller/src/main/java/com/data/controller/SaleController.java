package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Sale;
import com.data.dto.CommonDTO;
import com.data.service.ISaleService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/sale")
@Api(tags = "销售数据接口")
@CrossOrigin(origins="*", maxAge=3600)
public class SaleController {
	
	private static final Logger logger = LoggerFactory.getLogger(SaleController.class);

	@Autowired
	private ISaleService saleService;
	
	/**
	 * python抓取销售数据
	 * @param param
	 * @return
	 * @throws Exception 
	 * @throws IOException, DataException 
	 */
	@RequestMapping(value = "/getDataByWeb", method = RequestMethod.GET)
	@ApiOperation(value = "python抓取销售数据", httpMethod = "GET")
	public String getDataByWeb(String queryDate, Integer limit, 
			@RequestParam(value = "sysId", required = true)String sysId) throws Exception{
		ResultUtil result = saleService.getSaleByWeb(queryDate, sysId, limit);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 多条件分页查询销售数据
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getDataByParam", method = RequestMethod.GET)
	@ApiOperation(value = "多条件分页查询销售数据", httpMethod = "GET")
	public String getDataByParam(CommonDTO common, Sale sale, Integer page, Integer limit) throws Exception {
		ResultUtil result = saleService.getSaleByParam(common, sale, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 数据导出
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value = "/storeDailyExcel", method = RequestMethod.GET)
	@ApiOperation(value = "数据导出", httpMethod = "GET")
	public void storeDailyExcel(String system, String region, String province, String store, HttpServletResponse response) throws Exception {
		saleService.storeDailyexcel(system, region, province, store, response);
	}
	/**
	 * 选择字段导出销售数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/exportSaleExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出销售数据表", httpMethod = "GET")
	public void expertSaleExcel(Sale sale, CommonDTO common, HttpServletResponse response,
			@RequestParam(value = "stockNameStr", required = true)String stockNameStr) throws Exception {
		String fileName = "销售处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		saleService.exportSaleExcel(stockNameStr, common, sale, output);
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
     * 手动补全当前日期之前的门店销售额
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/calculateStoreDailySale", method = RequestMethod.GET)
    public String calculateStoreDailySale() throws Exception {
    	ResultUtil result;
		try {
			result = saleService.calculateStoreDailySale();			
		} catch (Exception e) {
			logger.error("--->>>日销售定时任务异常<<<---");
			result = ResultUtil.error("--->>>日销售定时任务异常<<<---");
		}
		return FastJsonUtil.objectToString(result);
    }
    
    /**
     * 查询日报表数据
     * @param system
     * @param region
     * @param province
     * @param store
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/queryStoreDailySaleReport", method = RequestMethod.GET)
    public String queryStoreDailySaleReport(String system, String region, String province, String store, Integer page, Integer limit) throws Exception {
    	ResultUtil result = saleService.queryStoreDailySaleReport(system, region, province, store, page, limit);
    	return FastJsonUtil.objectToString(result);
    }
    @RequestMapping(value = "/exportCompanyExcelBySys", method = RequestMethod.GET)
    @ApiOperation(value = "按系统-导出公司一级表", httpMethod = "GET")
    public void exportCompanyExcelBySys(String queryDate, HttpServletResponse response) throws Exception {
    	String fileName = "按系统-公司一级表" + DateUtil.getCurrentDateStr();
    	setResponseHeader(response, fileName);
    	OutputStream output = response.getOutputStream();
    	saleService.exportCompanyExcelBySys(queryDate, output);
    }
    @RequestMapping(value = "/exportRegionFirstExcelBySys", method = RequestMethod.GET)
    @ApiOperation(value = "按系统-导出区域表一级表", httpMethod = "GET")
    public void exportRegionFirstExcelBySys(String queryDate, String sysId, HttpServletResponse response) throws Exception {
    	String fileName = "按系统-导出区域表一级表" + DateUtil.getCurrentDateStr();
    	setResponseHeader(response, fileName);
    	OutputStream output = response.getOutputStream();
    	saleService.exportRegionFirstExcelBySys(queryDate, sysId, output);
    }
    @RequestMapping(value = "/exportRegionSecondExcelBySys", method = RequestMethod.GET)
    @ApiOperation(value = "按系统-导出区域表二级表", httpMethod = "GET")
    public void exportRegionSecondExcelBySys(String queryDate, String sysId, String region, HttpServletResponse response) throws Exception {
    	String fileName = "按系统-导出区域表二级表" + DateUtil.getCurrentDateStr();
    	setResponseHeader(response, fileName);
    	OutputStream output = response.getOutputStream();
    	saleService.exportRegionSecondExcelBySys(queryDate, sysId, region, output);
    }
    
    /**
     * 按区域导出公司一级表
     * @param saleDate
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportSaleExcelByRegion", method = RequestMethod.GET)
    @ApiOperation(value = "按区域-导出公司一级表", httpMethod = "GET")
    public void exportSaleExcelByRegion(String saleDate, HttpServletResponse response) throws Exception {
    	saleService.exportSaleExcelByRegion(saleDate, response);
    }
}
