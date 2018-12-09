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
	
	/**
	 * 分页集合列表
	 * @param page
	 * @param limit
	 * @return
	 */
	ResultUtil queryFunctionByPage(String page, String limit) throws Exception ;
	
	/**
	 * 查询权限信息详情
	 * @param functionId
	 * @return
	 */
	ResultUtil queryFunctionDetail(String functionId);
	
	/**
	 * 添加权限
	 * @param function
	 * @return
	 */
	ResultUtil addFunction(SystemFunction function);
	
	/**
	 * 更新权限
	 * @param function
	 * @return
	 */
	ResultUtil updateFunction(SystemFunction function);
	
	/**
	 * 删除权限
	 * @param functionId
	 * @return
	 */
	ResultUtil deleteFunction(String functionId);
	
	/**
	 * 批量删除
	 * @param functionIds
	 * @return
	 */
	ResultUtil batchDeleteFunction(String functionIds);
}
