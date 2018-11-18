package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.SimpleCode;
import com.data.service.ISimpleCodeService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/simpleCode")
@Api(tags = {"标准条码数据接口"})
@CrossOrigin(origins="*", maxAge=3600)
public class SimpleCodeController {
	
	@Autowired
	private ISimpleCodeService simpleCodeService;

	@RequestMapping(value = "/getSimpleCodeByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件分页查询标准条码信息", httpMethod = "POST")
	public String getSimpleCodeByParam(SimpleCode simpleCode,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception {
		ResultUtil result = simpleCodeService.getSimpleCodeByParam(simpleCode, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	
	@RequestMapping(value = "/updateSimpleCode", method = RequestMethod.PUT)
	@ApiOperation(value = "更新标准条码信息", httpMethod = "PUT")
	public String updateSimpleCode(SimpleCode simpleCode) {
		ResultUtil result = simpleCodeService.updateSimpleCode(simpleCode);
		return FastJsonUtil.objectToString(result);
	}
	
	@RequestMapping(value = "insertSimpleCode", method = RequestMethod.POST)
	@ApiOperation(value = "插入标准条码信息", httpMethod = "POST")
	public String insertSimpleCode(SimpleCode simpleCode) {
		ResultUtil result = simpleCodeService.insertSimpleCode(simpleCode);
		return FastJsonUtil.objectToString(result);
	}
	
	@RequestMapping(value = "deleteSimpleCodeById", method = RequestMethod.DELETE)
	@ApiOperation(value = "根据ID删除标准条码信息", httpMethod = "DELETE")
	public String deleteSimpleCodeById(int id) {
		ResultUtil result = simpleCodeService.deleteSimpleCode(id);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 按照id查询标准条码的基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/querySimpleCodeInfo", method = RequestMethod.POST)
	@ApiOperation(value = "按ID查询产品信息", httpMethod = "POST")
	public String querySimpleCodeInfo(String id) {
		ResultUtil result = simpleCodeService.querySimpleCodeInfo(id);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
     * 条码数据导入
     * @param file
     * @param request
     * @throws Exception 
     */
    @RequestMapping(value = "/uploadSimpleCodeExcel", method = RequestMethod.POST)
    @ApiOperation(value = "条码数据导入", httpMethod = "POST")
    public String uploadSimpleCodeExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	ResultUtil result = simpleCodeService.uploadSimpleCodeData(file);
    	return FastJsonUtil.objectToString(result);
    }
}
