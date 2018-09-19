package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Stock;
import com.data.dto.CommonDTO;
import com.data.service.IStockService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/stock")
// @Api(tags = {"库存数据接口"})
public class StockController {
	
	@Autowired
	private IStockService stockServiceImpl;

	/**
	 * 抓取库存数据
	 * @param common
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "getStockByWeb", method = RequestMethod.POST)
	@ApiOperation(value = "抓取库存数据", httpMethod = "POST")
	@ApiImplicitParam(name = "sysId", value = "系统ID", required = true)
	public String getStockByWeb(@RequestParam(required = false) CommonDTO common,
			@RequestParam(value = "sysId")int sysId, 
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws IOException {
		ResultUtil result = stockServiceImpl.getStockByWeb(common, sysId, page, limit);
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
	 * 生成门店单品表
	 * @param queryDate
	 * @param storeName
	 * @return
	 */
	@RequestMapping(value = "createStoreProductExcel", method = RequestMethod.POST)
	@ApiOperation(value = "生成门店单品表", httpMethod = "POST")
	public String createStoreProductExcel(String queryDate, String storeName, 
			HttpServletResponse response) {
		ResultUtil result = stockServiceImpl.createStoreProductExcel(queryDate, storeName);
		
		// 请求成功
		if (!result.getCode().equals("99")) {
			SXSSFWorkbook wb = (SXSSFWorkbook) result.getData();
			String fileName = "门店单品表" + queryDate;
			
			// 设置响应头
			setResponseHeader(response, fileName);
			OutputStream output = null;
			try {
				output = response.getOutputStream();
				wb.write(output);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != output) {
					try {
						output.flush();
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
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
