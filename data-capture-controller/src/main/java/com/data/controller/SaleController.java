package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
//@CrossOrigin(origins="*", maxAge=3600)
public class SaleController {

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
	@RequestMapping(value = "/storeDailyexcel", method = RequestMethod.GET)
	@ApiOperation(value = "数据导出", httpMethod = "GET")
	public void storeDailyexcel(String system, String region, String province, String store, HttpServletResponse response) throws Exception {
		saleService.storeDailyexcel(system, region, province, store, response);
	}
	/**
	 * 选择字段导出销售数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 */
	@RequestMapping(value = "/exportSaleExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出销售数据表", httpMethod = "GET")
	public void expertSaleExcel(Sale sale, CommonDTO common, HttpServletResponse response,
			@RequestParam(value = "stockNameStr", required = true)String stockNameStr) {
		String fileName = "销售处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			saleService.exportSaleExcel(stockNameStr, common, sale, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
    
    /**
     * 手动补全当前日期之前的门店销售额
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/calculateStoreDailySale", method = RequestMethod.GET)
    public String calculateStoreDailySale() throws Exception {
    	ResultUtil result = saleService.calculateStoreDailySale();
    	return FastJsonUtil.objectToString(result);
    }
}
