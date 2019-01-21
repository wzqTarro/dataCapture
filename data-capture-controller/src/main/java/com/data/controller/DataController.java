package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.service.IDataService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "数据操作接口")
@RestController(value = "/data")
public class DataController {
	
	@Autowired
	private IDataService dataService;

	@RequestMapping(value = "/completionData", method = RequestMethod.POST)
	@ApiOperation(value = "补全数据", httpMethod="POST")
	public String completionData() throws Exception {
		ResultUtil result = dataService.completionData();
		return FastJsonUtil.objectToString(result);
	}
}
