package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.SystemMenu;
import com.data.service.ISystemFunctionService;
import com.data.service.ISystemMenuService;
import com.data.service.ISystemRoleFunctionService;
import com.data.service.ISystemRoleMenuService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;



/**
 * 系统统一控制类
 * @author Alex
 *
 */
@RestController
@RequestMapping("/system")
@Api(tags = "系统全局管理接口")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SystemController {

	@Autowired
	private ISystemMenuService menuService;
	
	@Autowired
	private ISystemRoleFunctionService roleFunctionService;
	
	@Autowired
	private ISystemFunctionService functionService;
	
	@Autowired
	private ISystemRoleMenuService roleMenuService;
	
	/**
	 * 获取用户动作权限列表
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/buildMenuList", method = RequestMethod.POST)
	@ApiOperation(value = "构建菜单列表集合", httpMethod = "POST")
	public String buildMenuList(String roleId) {
		ResultUtil result = menuService.buildMenuList(roleId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 添加目录
	 * @param menu
	 * @return
	 */
	@RequestMapping(value = "/addMenu", method = RequestMethod.POST)
	@ApiOperation(value = "添加菜单", httpMethod = "POST")
	public String addMenu(SystemMenu menu) {
		ResultUtil result = menuService.addMenu(menu);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 更新目录
	 * @param menu
	 * @return
	 */
	@RequestMapping(value = "/updateMenu", method = RequestMethod.POST)
	@ApiOperation(value = "更新菜单", httpMethod = "POST")
	public String updateMenu(SystemMenu menu) {
		ResultUtil result = menuService.updateMenu(menu);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 删除目录
	 * @param menuId
	 * @return
	 */
	@RequestMapping(value = "/deleteMenu", method = RequestMethod.POST)
	@ApiOperation(value = "删除菜单", httpMethod = "POST")
	public String deleteMenu(String menuId) {
		ResultUtil result = menuService.deleteMenu(menuId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 分页显示目录列表
	 * @param page
	 * @param limit
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/queryMenuListByPage", method = RequestMethod.POST)
	@ApiOperation(value = "分页显示菜单列表集合", httpMethod = "POST")
	public String queryMenuListByPage(String page, String limit) throws Exception {
		ResultUtil result = menuService.queryMenuList(page, limit);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 根据roleId得到权限列表
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/queryRoleMenuList", method = RequestMethod.POST)
	@ApiOperation(value = "查询角色目录集合", httpMethod = "POST")
	public String queryRoleMenuList(String roleId) {
		ResultUtil result = menuService.queryRoleMenuFunctionList(roleId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 根据角色id查询角色权限集合
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/queryRoleFunctionByRoleId", method = RequestMethod.POST)
	@ApiOperation(value = "根据角色id查询角色权限集合", httpMethod = "POST")
	public String queryRoleFunctionByRoleId(String roleId) {
		ResultUtil result = functionService.queryRoleFunctionList(roleId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 更新角色权限
	 * @param roleId
	 * @param functionIds
	 * @return
	 */
	@RequestMapping(value = "/updateRoleFunction", method = RequestMethod.POST)
	@ApiOperation(value = "更新角色权限", httpMethod = "POST")
	public String updateRoleFunction(String roleId, String functionIds) {
		ResultUtil result = roleFunctionService.updateRoleFunction(roleId, functionIds);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 获取所有权限
	 * @return
	 */
	@RequestMapping(value = "/queryAllFunctionList", method = RequestMethod.GET)
	@ApiOperation(value = "获取所有权限", httpMethod = "GET")
	public String queryAllFunctionList() {
		ResultUtil result = functionService.queryAllFunctionList();
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 更新角色目录权限
	 * @param menuIds
	 * @return
	 */
	@RequestMapping(value = "/updateRoleMenu", method = RequestMethod.POST)
	@ApiOperation(value = "更新角色目录", httpMethod = "POST")
	public String updateRoleMenu(String roleId, String menuIds) {
		ResultUtil result = roleMenuService.updateRoleMenu(roleId, menuIds);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 查看角色目录
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/queryRoleMenu", method = RequestMethod.GET)
	@ApiOperation(value = "查看角色目录", httpMethod = "GET")
	public String queryRoleMenu(String roleId) {
		ResultUtil result = roleMenuService.queryRoleMenu(roleId);
		return FastJsonUtil.objectToString(result);
	}

}
