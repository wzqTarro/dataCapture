package com.data.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.Reject;
import com.data.dto.CommonDTO;
import com.data.model.RejectModel;
import com.data.service.IRejectService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/reject")
@Api(tags = {"退单数据接口"})
@CrossOrigin(origins="*", maxAge=3600)
public class RejectController {
	
	@Autowired
	private IRejectService rejectService;
	
	/**
	 * 抓取订单数据
	 * @param queryDate
	 * @param sysId
	 * @param page
	 * @param limit
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/getRejectByWeb", method = RequestMethod.GET)
	@ApiOperation(value = "抓取退单数据", httpMethod = "GET")
	public String getRejectByWeb(String queryDate, Integer limit, 
			@RequestParam(value = "id", required = true)Integer id) throws Exception {
		ResultUtil result = rejectService.getRejectByWeb(queryDate, id, limit);
		return FastJsonUtil.objectToString(result);
	}
	@RequestMapping(value = "/getRejectByIds", method = RequestMethod.GET)
	@ApiOperation(value = "批量抓取退单数据", httpMethod = "GET")
	public String getRejectByIds(String queryDate, String ids) throws Exception {
		ResultUtil result = rejectService.getRejectByIds(queryDate, ids);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 分页多条件查询
	 * @param order
	 * @param common
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getRejectByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "分页多条件查询退单", httpMethod = "GET")
	public String getRejectByCondition(Reject reject, CommonDTO common, Integer page, Integer limit) throws Exception {
		ResultUtil result = rejectService.getRejectByParam(common, reject, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 选择字段导出退单数据表
	 * @param stockNameStr
	 * @param common
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/exportRejectExcel", method = RequestMethod.GET)
	@ApiOperation(value = "选择字段导出退单数据表", httpMethod = "GET")
	public void expertRejectExcel(Reject reject, CommonDTO common, HttpServletResponse response,
			@RequestParam(value = "stockNameStr", required = true)String stockNameStr) throws Exception {
		String fileName = "退单处理表" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
		
		// 设置响应头
		setResponseHeader(response, fileName);
		OutputStream output = response.getOutputStream();
		rejectService.exportRejectExcel(stockNameStr, common, reject, output);
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
     * 退单报警集合列表
     * @param reject
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/queryRejectAlarmList", method = RequestMethod.GET)
    @ApiOperation(value = "退单报警集合列表", httpMethod = "GET")
    public String queryRejectAlarmList(CommonDTO common, RejectModel reject, Integer page, Integer limit) throws Exception {
    	ResultUtil result = rejectService.queryRejectAlarmList(common, reject, page, limit);
    	return FastJsonUtil.objectToString(result);
    }
    
    /**
     * 警报报表输出
     * @param order
     * @param response
     * @throws Exception 
     */
    @RequestMapping(value = "/rejectAlarmListExcel", method = RequestMethod.GET)
    @ApiOperation(value = "警报报表输出", httpMethod = "GET")
    public void rejectAlarmListExcel(CommonDTO common, RejectModel reject, HttpServletResponse response) throws Exception {
    	rejectService.rejectAlarmListExcel(common, reject, response);
    }
    
    /**
     * 退单数据导入
     * @param file
     * @param request
     * @throws Exception 
     */
    @RequestMapping(value = "/uploadRejectExcel", method = RequestMethod.POST)
    @ApiOperation(value = "退单数据导入", httpMethod = "POST")
    public String uploadRejectExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	ResultUtil result = rejectService.uploadRejectData(file);
    	return FastJsonUtil.objectToString(result);
    }
}
