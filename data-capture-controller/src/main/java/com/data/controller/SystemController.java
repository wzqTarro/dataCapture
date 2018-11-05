package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.SystemMenu;
import com.data.service.ISystemMenuService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;


/**
 * 系统统一控制类
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/system")
public class SystemController {

	@Autowired
	private ISystemMenuService menuService;
	
	/**
	 * 获取用户动作权限列表
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/buildMenuList", method = RequestMethod.POST)
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
	public String queryRoleMenuList(String roleId) {
		ResultUtil result = menuService.queryRoleMenuFunctionList(roleId);
		return FastJsonUtil.objectToString(result);
	}

}
