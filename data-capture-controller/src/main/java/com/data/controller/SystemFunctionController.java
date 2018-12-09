package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.SystemFunction;
import com.data.service.ISystemFunctionService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.ApiOperation;

/**
 * 权限控制器
 * @author Alex
 *
 */
@RestController
@RequestMapping("/function")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SystemFunctionController {

	@Autowired
	private ISystemFunctionService functionService;
	
	/**
	 * 分页查询权限集合列表
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/queryFunctionByPage", method = RequestMethod.GET)
	@ApiOperation(value = "分页查询权限集合列表", httpMethod = "GET")
	public String queryFunctionByPage(String page, String limit) throws Exception {
		ResultUtil result = functionService.queryFunctionByPage(page, limit);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 查询权限详情
	 * @param functionId
	 * @return
	 */
	@RequestMapping(value = "/queryFunctionDetail", method = RequestMethod.GET)
	@ApiOperation(value = "查询权限详情", httpMethod = "GET")
	public String queryFunctionDetail(String functionId) {
		ResultUtil result = functionService.queryFunctionDetail(functionId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 添加详情
	 * @param function
	 * @return
	 */
	@RequestMapping(value = "/addFunction", method = RequestMethod.POST)
	@ApiOperation(value = "查询权限详情", httpMethod = "POST")
	public String addFunction(SystemFunction function) {
		ResultUtil result = functionService.addFunction(function);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 更新权限详情
	 * @param function
	 * @return
	 */
	@RequestMapping(value = "/updateFunction", method = RequestMethod.POST)
	@ApiOperation(value = "更新权限详情", httpMethod = "POST")
	public String updateFunction(SystemFunction function) {
		ResultUtil result = functionService.updateFunction(function);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 删除权限详情
	 * @param functionId
	 * @return
	 */
	@RequestMapping(value = "/deleteFunction", method = RequestMethod.POST)
	@ApiOperation(value = "删除权限详情", httpMethod = "POST")
	public String deleteFunction(String functionId) {
		ResultUtil result = functionService.deleteFunction(functionId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 批量删除权限详情
	 * @param functionIds
	 * @return
	 */
	@RequestMapping(value = "/batchDeleteFunction", method = RequestMethod.POST)
	@ApiOperation(value = "批量删除权限详情", httpMethod = "POST")
	public String batchDeleteFunction(String functionIds) {
		ResultUtil result = functionService.batchDeleteFunction(functionIds);
		return FastJsonUtil.objectToString(result);
	}
}
