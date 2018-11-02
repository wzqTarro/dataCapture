package com.data.service;

import java.util.List;

import com.data.bean.SystemFunction;

public interface ISystemFunctionService {

	/**
	 * 根据角色id查询工作按钮集合
	 * @param roleId
	 * @return
	 */
	List<SystemFunction> queryFunctionList(String roleId);
}
