package com.data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.data.bean.SystemFunction;
import com.data.bean.SystemMenu;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.exception.GlobalException;
import com.data.service.ISystemFunctionService;
import com.data.utils.CommonUtil;
import com.data.utils.ResultUtil;

@Service("systemFunctionService")
public class SystemFunctionServiceImpl extends CommonServiceImpl implements ISystemFunctionService {

	private static final Logger logger = LoggerFactory.getLogger(SystemFunctionServiceImpl.class);
	
	@Override
	public List<SystemFunction> queryFunctionList(String roleId) {
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		return queryListByObject(QueryId.QUERY_FUNCTION_BY_ROLE_ID, params);
	}

	@Override
	public ResultUtil queryRoleFunctionList(String roleId) {
		logger.info("--->>>查询角色权限工作按钮角色为:{} <<<---", roleId);
		if(CommonUtil.isBlank(roleId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色编号不能为空!");
		}
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		List<SystemFunction> functionList;
		functionList = queryListByObject(QueryId.QUERY_ROLE_FUNCTION_LIST_BY_ROLE_ID, params);
		if(CommonUtil.isBlank(functionList)) {
			functionList = new ArrayList<>(10); 
		}
		ResultUtil result = ResultUtil.success();
		result.setData(functionList);
		return result;
	}

	@Override
	public ResultUtil queryAllFunctionList() {
		//所有menu
		List<SystemMenu> menuList = queryListByObject(QueryId.QUERY_MENU_LIST_BY_PAGE, null);
		//父menu
		Set<SystemMenu> parentMenuSet = new HashSet<>(10);
		for(SystemMenu menu : menuList) {
			if(CodeEnum.CODE_VALUE_00_ENUM.value().equals(menu.getIsParent())) {
				parentMenuSet.add(menu);
			}
		}
		//构建子目录
		buildChildMenu(menuList, parentMenuSet);
		buildMenuFunctionList(parentMenuSet);
		List<SystemFunction> functionList = queryListByObject(QueryId.QUERY_ALL_FUNCTION_LIST, null);
		Map<String, Object> resultMap = new HashMap<>(4);
		resultMap.put("menuList", parentMenuSet);
		resultMap.put("roleFunction", functionList);
		return ResultUtil.success(resultMap);
	}
	
	private void buildChildMenu(List<SystemMenu> menuList, Set<SystemMenu> parentMenuSet) {
		Map<String, Object> parentMenuMap = new HashMap<>(10);
		for(SystemMenu menu : parentMenuSet) {
			parentMenuMap.put(menu.getMenuId(), menu);
		}
		
		for(int i = 0, size = menuList.size(); i < size; i++) {
			SystemMenu childMenu = menuList.get(i);
			if(CodeEnum.CODE_VALUE_01_ENUM.value().equals(childMenu.getIsParent())) {
				String parentId = childMenu.getParentId();
				SystemMenu parentMenu = (SystemMenu) parentMenuMap.get(parentId);
				List<SystemMenu> childMenuList = parentMenu.getChildMenuList();
				if(childMenuList == null) {
					childMenuList = new ArrayList<>(10);
				}
				childMenuList.add(childMenu);
				parentMenu.setChildMenuList(childMenuList);
			}
		}
		parentMenuSet.clear();
		for(Map.Entry<String, Object> entry : parentMenuMap.entrySet()) {
			parentMenuSet.add((SystemMenu) entry.getValue());
		}
	}
	
	private void buildMenuFunctionList(Set<SystemMenu> parentMenuSet) {
		Map<String, Object> params = new HashMap<>(4);
		for(SystemMenu menu : parentMenuSet) {
			List<SystemMenu> childMenuList = menu.getChildMenuList();
			if(childMenuList != null && !childMenuList.isEmpty()) {
				for(SystemMenu childMenu : childMenuList) {
					String menuId = childMenu.getMenuId();
					params.put("menuId", menuId);
					List<SystemFunction> functionList = queryListByObject(QueryId.QUERY_MENU_FUNCTION_LIST_BY_MENU_ID, params);
					childMenu.setFunctionList(functionList);
				}
			}
		}
	}
	
}
