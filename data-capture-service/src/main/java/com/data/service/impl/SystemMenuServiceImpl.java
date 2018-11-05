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
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.CodeEnum;
import com.data.exception.GlobalException;
import com.data.service.ISystemMenuService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

@Service("systemMenuService")
public class SystemMenuServiceImpl extends CommonServiceImpl implements ISystemMenuService {

	private static final Logger logger = LoggerFactory.getLogger(SystemMenuServiceImpl.class);
	
	@Override
	public ResultUtil buildMenuList(String roleId) {
		logger.info("--->>>当前用户角色为: {}<<<---", roleId);
		if(CommonUtil.isBlank(roleId)) {
			return ResultUtil.error("角色编号不能为空，系统异常！");
		}
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		Map<String, Boolean> authMap = new HashMap<>(10);
		List<SystemFunction> functionList = queryListByObject(QueryId.QUERY_FUNCTION_BY_ROLE_ID, params);
		for(SystemFunction function : functionList) {
			authMap.put(function.getFunctionAuth(), true);
		}
		List<SystemMenu> roleMenuList = queryListByObject(QueryId.QUERY_ROLE_MENU_BY_ROLE_ID, params);
		Set<SystemMenu> parentMenuSet = new HashSet<>(10);
		for(SystemMenu menu : roleMenuList) {
			if("00".equals(menu.getIsParent())) {
				parentMenuSet.add(menu);
			}
		}
		List<SystemMenu> userMenuList = new ArrayList<>(10);
		userMenuList = buildChildMenu(userMenuList, parentMenuSet, roleMenuList);
		Map<String, Object> menuMap = new HashMap<>(4);
		menuMap.put("menuList", userMenuList);
		menuMap.put("auth", authMap);
		return ResultUtil.success(menuMap);
	}
	
	/**
	 * 组建子目录
	 * @param userMenuList
	 * @param parentMenuSet
	 * @param roleMenuList
	 * @return
	 */
	private List<SystemMenu> buildChildMenu(List<SystemMenu> userMenuList, Set<SystemMenu> parentMenuSet, List<SystemMenu> roleMenuList) {
		Map<String, Object> parentMenuMap = new HashMap<>(10);
		for(SystemMenu menu : parentMenuSet) {
			parentMenuMap.put(menu.getMenuId(), menu);
		}
		
		for(int i = 0, size = roleMenuList.size(); i < size; i++) {
			SystemMenu childMenu = roleMenuList.get(i);
			if("01".equals(childMenu.getIsParent())) {
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
		
		for(Map.Entry<String, Object> entry : parentMenuMap.entrySet()) {
			userMenuList.add((SystemMenu) entry.getValue());
		}
		return userMenuList;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil addMenu(SystemMenu menu) {
		Map<String, Object> params = validateMenuParameter(menu);
		logger.info("--->>>添加目录参数为: {}<<<---", FastJsonUtil.objectToString(params));
		int count = insert(InsertId.INSERT_ADD_NEW_MENU, params);
		return ResultUtil.success(count);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateMenu(SystemMenu menu) {
		Map<String, Object> params = new HashMap<>(10);
		if(CommonUtil.isBlank(menu.getMenuId())) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录编号不能为空！");
		}
		if(CommonUtil.isOverLength(menu.getMenuName(), 16)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录名称长度不能超过16位！");
		}
		if(CommonUtil.isOverLength(menu.getMenuIcon(), 128)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录图标长度不能超过128位！");
		}
		if(CommonUtil.isOverLength(menu.getMenuUrl(), 64)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录路径长度不能超过64位！");
		}
		params.put("menuId", menu.getMenuId());
		if(CommonUtil.isNotBlank(menu.getMenuName())) {
			params.put("menuName", menu.getMenuName());
		}
		if(CommonUtil.isNotBlank(menu.getMenuIcon())) {
			params.put("menuIcon", menu.getMenuIcon());
		}
		if(CommonUtil.isNotBlank(menu.getMenuUrl())) {
			params.put("menuUrl", menu.getMenuUrl());
		}
		if(CommonUtil.isNotBlank(menu.getIsParent())) {
			params.put("isParent", menu.getIsParent());
		}
		if(CommonUtil.isNotBlank(menu.getParentId())) {
			params.put("parentId", menu.getParentId());
		}
		if(CommonUtil.isNotBlank(menu.getIsDelete())) {
			params.put("isDelete", menu.getIsDelete());
		}
		if(CommonUtil.isNotBlank(menu.getIsEnable())) {
			params.put("isEnable", menu.getIsEnable());
		}
		logger.info("--->>>更新目录参数为: {}<<<---", FastJsonUtil.objectToString(params));
		int count = update(UpdateId.UPDATE_MENU_BY_MENU_ID, params);
		return ResultUtil.success(count);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil deleteMenu(String menuId) {
		if(CommonUtil.isBlank(menuId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录编号不能为空！");
		}
		Map<String, Object> params = new HashMap<>(10);
		params.put("isDelete", "01");
		params.put("isEnable", "01");
		int count = update(UpdateId.UPDATE_MENU_BY_MENU_ID, params);
		return ResultUtil.success(count);
	}
	
	private Map<String, Object> validateMenuParameter(SystemMenu menu) {
		if(CommonUtil.isBlank(menu)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "传入目录对象不能为空，系统异常！");
		}
		Map<String, Object> params = new HashMap<>(10);
		String menuId = menu.getMenuId();
		if(CommonUtil.isBlank(menuId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录编号不能为空！");
		}
		params.put("menuId", menuId);
		//反查id是否可用
		int count = queryCountByObject(QueryId.QUERY_COUNT_MENU_BY_MENU_ID, params);
		if(count == 0) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "该目录编号已使用，请更换！");
		}
		if(CommonUtil.isOverLength(menuId, 16)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录编号长度不能超过16位！");
		}
		String menuName = menu.getMenuName();
		if(CommonUtil.isBlank(menuName)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录名称不能为空！");
		}
		if(CommonUtil.isOverLength(menuName, 16)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录名称长度不能超过16位！");
		}
		params.put("menuName", menuName);
		String menuUrl = menu.getMenuUrl();
		if(CommonUtil.isBlank(menuUrl)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录路径不能为空！");
		}
		if(CommonUtil.isOverLength(menuUrl, 64)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录路径长度不能超过64位！");
		}
		params.put("menuUrl", menuUrl);
		String menuIcon = menu.getMenuIcon();
		if(CommonUtil.isNotBlank(menuIcon)) {
			if(CommonUtil.isOverLength(menuIcon, 128)) {
				throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "目录图标长度不能超过128位！");
			}
		}
		params.put("menuIcon", menuIcon);
		String isParent = menu.getIsParent();
		String parentId = menu.getParentId();
		if("01".equals(isParent)) {
			//如果是子目录 需要验证父级目录检验
			if(CommonUtil.isBlank(parentId)) {
				throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "父目录不能为空！");
			}
			if(CommonUtil.isOverLength(parentId, 16)) {
				throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "父目录编号长度不能超过16位！");
			}
			params.put("isParent", "01");
			params.put("parentId", parentId);
		} else if("00".equals(isParent)) {
			params.put("isParent", "00");
			params.put("parentId", "00");
		}
		params.put("isEnable", menu.getIsEnable());
		params.put("isDelete", "00");
		return params;
	}

	@Override
	public ResultUtil queryMenuList(String page, String limit) throws Exception {
		if(CommonUtil.isBlank(page)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "页码不能为空！");
		}
		if(CommonUtil.isBlank(limit)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "页面内容条数不能为空！");
		}
		PageRecord<SystemMenu> menuPage = queryPageByObject(QueryId.QUERY_COUNT_MENU_BY_PAGE, 
							QueryId.QUERY_MENU_LIST_BY_PAGE, null, page, limit);
		logger.info("--->>>目录分页集合为: {}<<<---", FastJsonUtil.objectToString(menuPage));
		return ResultUtil.success(menuPage);
	}

	@Override
	public ResultUtil queryRoleMenuFunctionList(String roleId) {
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		//所有menu
		List<SystemMenu> menuList = queryListByObject(QueryId.QUERY_MENU_LIST_BY_PAGE, null);
		//父menu
		Set<SystemMenu> parentMenuSet = new HashSet<>(10);
		for(SystemMenu menu : menuList) {
			if("00".equals(menu.getIsParent())) {
				parentMenuSet.add(menu);
			}
		}
		//构建子目录
		buildChildMenu(menuList, parentMenuSet);
		//构建方法按钮集合
		buildMenuFunctionList(parentMenuSet);
		//角色拥有的权限
		List<SystemFunction> functionList = queryListByObject(QueryId.QUERY_FUNCTION_BY_ROLE_ID, params);
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
			if("01".equals(childMenu.getIsParent())) {
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
