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
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.SystemFunction;
import com.data.bean.SystemMenu;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.CodeEnum;
import com.data.exception.GlobalException;
import com.data.service.ISystemFunctionService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
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

	@Override
	public ResultUtil queryFunctionByPage(String page, String limit) throws Exception {
		if(CommonUtil.isBlank(page)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "页码不能为空！");
		}
		if(CommonUtil.isBlank(limit)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "内容数不能为空！");
		}
		PageRecord<SystemFunction> functionPage = queryPageByObject(QueryId.QUERY_COUNT_FUNCTION_PAGE,
					QueryId.QUERY_LIST_FUNCTION_PAGE, null, page, limit); 
		return ResultUtil.success(functionPage);
	}

	@Override
	public ResultUtil queryFunctionDetail(String functionId) {
		if(CommonUtil.isBlank(functionId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "查询权限编号不能为空！");
		}
		Map<String, Object> params = new HashMap<>(4);
		params.put("functionId", functionId);
		SystemFunction function = (SystemFunction) queryObjectByParameter(QueryId.QUERY_FUNCTION_BY_FUNCTION_ID, params);
		return ResultUtil.success(function);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil addFunction(SystemFunction function) {
		Map<String, Object> params = validateFunctionParameter(function);
		logger.info("--->>>添加权限的参数为: {}<<<---", FastJsonUtil.objectToString(function));
		return ResultUtil.success(insert(InsertId.INSERT_NEW_SYSTEM_FUNCTION, params));
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateFunction(SystemFunction function) {
		Map<String, Object> params = new HashMap<>(10);
		String functionId = function.getFunctionId();
		if(CommonUtil.isBlank(functionId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限编号不能为空！");
		}
		if(CommonUtil.isOverLength(functionId, 32)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限编号长度不能超过32位！");
		}
		String functionName = function.getFunctionName();
		if(CommonUtil.isOverLength(functionName, 24)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限名称长度不能超过32位！");
		}
		String functionUrl = function.getFunctionUrl();
		if(CommonUtil.isOverLength(functionUrl, 64)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限链接长度不能超过32位！");
		}
		String functionAuth = function.getFunctionAuth();
		if(CommonUtil.isOverLength(functionAuth, 32)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限标识长度不能超过32位！");
		}
		params.put("functionId", functionId);
		if(CommonUtil.isNotBlank(functionName)) {
			params.put("functionName", functionName);
		}
		if(CommonUtil.isNotBlank(function.getFunctionIcon())) {
			params.put("functionIcon", function.getFunctionIcon());
		}
		if(CommonUtil.isNotBlank(functionUrl)) {
			params.put("functionUrl", functionUrl);
		}
		if(CommonUtil.isNotBlank(functionAuth)) {
			params.put("functionAuth", functionAuth);
		}
		if(CommonUtil.isNotBlank(function.getFunctionMethod())) {
			params.put("functionMethod", function.getFunctionMethod());
		}
		if(CommonUtil.isNotBlank(function.getIsEnable())) {
			params.put("isEnable", function.getIsEnable());
		}
		if(CommonUtil.isNotBlank(function.getIsDelete())) {
			params.put("isDelete", function.getIsDelete());
		}
		logger.info("--->>>权限更新的参数为:{}<<<---", FastJsonUtil.objectToString(params));
		return ResultUtil.success(update(UpdateId.UPDATE_SYSTEM_FUNCTION_BY_FUNCTION_ID, params));
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil deleteFunction(String functionId) {
		if(CommonUtil.isBlank(functionId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限编号不能为空！");
		}
		Map<String, Object> params = new HashMap<>(4);
		params.put("functionId", functionId);
		return ResultUtil.success(delete(DeleteId.DELETE_SYSTEM_FUNCTION_BY_FUNCTION_ID, params));
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil batchDeleteFunction(String functionIds) {
		if(CommonUtil.isBlank(functionIds)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "多选权限编号集合不能为空！");
		}
		String[] ids = CommonUtil.parseIdsCollection(functionIds, ",");
		logger.info("--->>>批量删除权限请求参数为: {}<<<---", FastJsonUtil.objectToString(ids));
		Map<String, Object> params = new HashMap<>(4);
		for(int i = 0, length = ids.length; i < length; i++) {
			params.put("functionId", ids[i]);
			delete(DeleteId.DELETE_SYSTEM_FUNCTION_BY_FUNCTION_ID, params);
		}
		return ResultUtil.success();
	}
	
	private Map<String, Object> validateFunctionParameter(SystemFunction function) {
		if(CommonUtil.isBlank(function)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "参数传输有误！");
		}
		Map<String, Object> params = new HashMap<>(10);
		String functionId = function.getFunctionId();
		if(CommonUtil.isBlank(functionId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限编号不能为空！");
		}
		if(CommonUtil.isOverLength(functionId, 32)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限编号长度不能超过32位！");
		}
		params.put("functionId", functionId);
		SystemFunction tempFunction = (SystemFunction) queryObjectByParameter(QueryId.QUERY_FUNCTION_BY_FUNCTION_ID, params);
		if(CommonUtil.isNotBlank(tempFunction)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限编号已存在，请重试！");
		}
		String functionName = function.getFunctionName();
		if(CommonUtil.isBlank(functionName)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限名称不能为空！");
		}
		if(CommonUtil.isOverLength(functionName, 24)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限名称长度不能超过32位！");
		}
		params.put("functionName", functionName);
		String functionUrl = function.getFunctionUrl();
		if(CommonUtil.isBlank(functionUrl)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限链接不能为空！");
		}
		if(CommonUtil.isOverLength(functionUrl, 64)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限链接长度不能超过32位！");
		}
		params.put("functionUrl", functionUrl);
		String functionAuth = function.getFunctionAuth();
		if(CommonUtil.isBlank(functionAuth)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限标识不能为空！");
		}
		if(CommonUtil.isOverLength(functionAuth, 32)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限标识长度不能超过32位！");
		}
		params.put("functionAuth", functionAuth);
		SystemFunction authFunction = (SystemFunction) queryObjectByParameter(QueryId.QUERY_FUNCTION_BY_FUNCTION_AUTH, params);
		if(CommonUtil.isNotBlank(authFunction)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "权限标识已存在，请重试！");
		}
		String isEnable = function.getIsEnable();
		if(CommonUtil.isBlank(isEnable)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "是否可用参数不能为空！");
		}
		params.put("isEnable", isEnable);
		String isDelete = function.getIsDelete();
		if(CommonUtil.isBlank(isDelete)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "是否删除参数不能为空！");
		}
		params.put("isDelete", isDelete);
		return params;
	}
	
}
