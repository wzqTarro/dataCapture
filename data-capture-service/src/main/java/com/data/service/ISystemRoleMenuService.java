package com.data.service;

import com.data.utils.ResultUtil;

/**
 * 角色目录服务类
 * @author Alex
 *
 */
public interface ISystemRoleMenuService {

	ResultUtil updateRoleMenu(String roleId, String menuIds);
	
	ResultUtil queryRoleMenu(String roleId);
}
