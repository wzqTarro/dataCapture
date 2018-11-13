package com.data.service;

import com.data.utils.ResultUtil;

/**
 * 角色按钮服务类
 * @author Alex
 *
 *	注：
 *	权限逻辑这一块的操作流程为
 *	当更新角色权限关联表时 不是直接更新 而是将该权限旧的权限全部删掉 
 *	然后添加新的权限
 */
public interface ISystemRoleFunctionService {
	
	/**
	 * 更新角色权限
	 * @param roleId
	 * @param functionIds
	 * @return
	 */
	ResultUtil updateRoleFunction(String roleId, String functionIds);

	/**
	 * 添加新的角色权限
	 * @param functionIds
	 * @param roleId
	 * @return
	 */
	//ResultUtil addRoleFunction(String functionIds, String roleId);
	
	/**
	 * 删除旧的角色权限
	 * @param functionIds
	 * @param roleId
	 * @return
	 */
	//ResultUtil deleteRoleFunction(String functionIds, String roleId);
}
