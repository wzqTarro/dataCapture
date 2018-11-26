package com.data.service;

import java.util.List;

import com.data.bean.SystemFunction;
import com.data.utils.ResultUtil;

public interface ISystemFunctionService {

	/**
	 * 根据角色id查询工作按钮集合
	 * @param roleId
	 * @return
	 */
	List<SystemFunction> queryFunctionList(String roleId);
	
	/**
	 * 根据角色查询工作按钮权限集合
	 * @param roleId
	 * @return
	 */
	ResultUtil queryRoleFunctionList(String roleId);
	
	/**
	 * 获取所有的权限
	 * @return
	 */
	ResultUtil queryAllFunctionList();
}
