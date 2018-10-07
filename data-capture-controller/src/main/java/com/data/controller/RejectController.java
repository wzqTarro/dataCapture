package com.data.controller;

import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Reject;
import com.data.dto.CommonDTO;
import com.data.service.IRejectService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/reject")
@Api(tags = {"退单数据接口"})
//@CrossOrigin(origins="*", maxAge=3600)
public class RejectController {

	private static Logger logger = LoggerFactory.getLogger(RejectController.class);
	
	@Autowired
	private IRejectService rejectService;
	
	/**
	 * 抓取订单数据
	 * @param queryDate
	 * @param sysId
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/getRejectByWeb", method = RequestMethod.GET)
	@ApiOperation(value = "抓取退单数据", httpMethod = "GET")
	public String getRejectByWeb(String queryDate, Integer limit, 
			@RequestParam(value = "sysId", required = true)String sysId) {
		ResultUtil result = rejectService.getRejectByWeb(queryDate, sysId, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 分页多条件查询
	 * @param order
	 * @param common
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/getRejectByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "分页多条件查询退单", httpMethod = "GET")
	public String getRejectByCondition(Reject reject, CommonDTO common, Integer page, Integer limit) {
		ResultUtil result = ResultUtil.error();
		try {
			result = rejectService.getRejectByParam(common, reject, page, limit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 选择字段导出退单数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 */
	@RequestMapping(value = "/exportRejectExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出退单数据表", httpMethod = "GET")
	public void expertRejectExcel(Reject reject, CommonDTO common, HttpServletResponse response,
			@RequestParam(value = "stockNameStr", required = true)String stockNameStr) {
		String fileName = "退单处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output;
		try {
			output = response.getOutputStream();
			rejectService.exportRejectExcel(stockNameStr, common, reject, output);
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
     * 退单报警集合列表
     * @param reject
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/queryRejectAlarmList", method = RequestMethod.GET)
    public String queryRejectAlarmList(Reject reject, Integer page, Integer limit) throws Exception {
    	ResultUtil result = rejectService.queryRejectAlarmList(reject, page, limit);
    	return FastJsonUtil.objectToString(result);
    }
    
    /**
     * 警报报表输出
     * @param order
     * @param response
     * @throws Exception 
     */
    @RequestMapping(value = "/rejectAlarmListExcel", method = RequestMethod.GET)
    public void rejectAlarmListExcel(Reject reject, HttpServletResponse response) throws Exception {
    	rejectService.rejectAlarmListExcel(reject, response);
    }
}
