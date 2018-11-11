package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.SystemRole;
import com.data.service.ISystemRoleService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 角色控制器
 * @author Alex
 *
 */
@RestController
@RequestMapping("/systemRole")
@Api(tags = "系统角色全局接口")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SystemRoleController {

	@Autowired
	private ISystemRoleService systemRoleService;
	
	/**
	 * 角色分页列表
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/querySystemRoleByPage", method = RequestMethod.POST)
	@ApiOperation(value = "查询分页角色列表", httpMethod = "POST")
	public String querySystemRoleByPage(String page, String limit) throws Exception {
		ResultUtil result = systemRoleService.querySystemRoleByPage(page, limit);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 角色分页列表
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/querySystemRoleDetail", method = RequestMethod.POST)
	@ApiOperation(value = "查询单个角色详情", httpMethod = "POST")
	public String querySystemRoleDetail(String roleId) {
		ResultUtil result = systemRoleService.querySystemRoleDetail(roleId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 添加新角色
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/addSystemRole", method = RequestMethod.POST)
	@ApiOperation(value = "添加角色", httpMethod = "POST")
	public String addSystemRole(SystemRole role) {
		ResultUtil result = systemRoleService.addSystemRole(role);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 更新角色
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/updateSystemRole", method = RequestMethod.POST)
	@ApiOperation(value = "更新角色", httpMethod = "POST")
	public String updateSystemRole(SystemRole role) {
		ResultUtil result = systemRoleService.updateSystemRole(role);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 删除角色
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/deleteSystemRoleByRoleId", method = RequestMethod.POST)
	@ApiOperation(value = "删除角色", httpMethod = "POST")
	public String deleteSystemRoleByRoleId(String roleId) {
		ResultUtil result = systemRoleService.deleteSystemRoleByRoleId(roleId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 批量删除
	 * @param roleIds
	 * @return
	 */
	@RequestMapping(value = "/deleteSystemRoleByIds", method = RequestMethod.POST)
	@ApiOperation(value = "批量删除角色", httpMethod = "POST")
	public String deleteSystemRoleByIds(String roleIds) {
		ResultUtil result = systemRoleService.deleteSystemRoleByIds(roleIds);
		return FastJsonUtil.objectToString(result);
	}
	
}
