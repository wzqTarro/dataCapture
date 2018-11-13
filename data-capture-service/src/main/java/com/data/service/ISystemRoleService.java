package com.data.service;

import com.data.bean.SystemRole;
import com.data.utils.ResultUtil;

/**
 * 角色服务类
 * @author Alex
 *
 */
public interface ISystemRoleService {

	/**
	 * 分页角色列表
	 * @param page
	 * @param limit
	 * @return
	 */
	ResultUtil querySystemRoleByPage(String page, String limit) throws Exception;
	
	/**
	 * 查询单个角色信息
	 * @param roleId
	 * @return
	 */
	ResultUtil querySystemRoleDetail(String roleId);
	
	/**
	 * 添加角色
	 * @param role
	 * @return
	 */
	ResultUtil addSystemRole(SystemRole role);
	
	/**
	 * 更新角色
	 * @param role
	 * @return
	 */
	ResultUtil updateSystemRole(SystemRole role);
	
	/**
	 * 删除角色
	 * @param roleId
	 * @return
	 */
	ResultUtil deleteSystemRoleByRoleId(String roleId);
	
	/**
	 * 批量删除
	 * @param roldIds
	 * @return
	 */
	ResultUtil deleteSystemRoleByIds(String roleIds);
}
