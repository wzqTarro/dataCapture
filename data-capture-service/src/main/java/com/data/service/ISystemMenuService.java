package com.data.service;


import com.data.bean.SystemMenu;
import com.data.utils.ResultUtil;

public interface ISystemMenuService {

	/**
	 * 根据角色id得到目录权限列表
	 * @param roleId
	 * @return
	 */
	ResultUtil buildMenuList(String roleId);
	
	/**
	 * 添加目录
	 * @param menu
	 * @return
	 */
	ResultUtil addMenu(SystemMenu menu);
	
	/**
	 * 更新目录信息
	 * @param menu
	 * @return
	 */
	ResultUtil updateMenu(SystemMenu menu);
	
	/**
	 * 删除目录(逻辑删除)
	 * @param menuId
	 * @return
	 */
	ResultUtil deleteMenu(String menuId);
	
	/**
	 * 分页显示目录列表
	 * @param page
	 * @param limit
	 * @return
	 */
	ResultUtil queryMenuList(String page, String limit) throws Exception;
	
	/**
	 *  罗列权限列表
	 * @param roleId
	 * @return
	 */
	ResultUtil queryRoleMenuFunctionList(String roleId);
}
