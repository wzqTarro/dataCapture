package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.SimpleCode;
import com.data.service.ISimpleCodeService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/simpleCode")
@Api(tags = {"标准条码数据接口"})
public class SimpleCodeController {
	
	@Autowired
	private ISimpleCodeService simpleCodeService;

	@RequestMapping(value = "/getSimpleCodeByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件分页查询标准条码信息", httpMethod = "POST")
	public String getSimpleCodeByParam(@RequestBody String param) throws Exception {
		ResultUtil result = simpleCodeService.getSimpleCodeByParam(param);
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
}
