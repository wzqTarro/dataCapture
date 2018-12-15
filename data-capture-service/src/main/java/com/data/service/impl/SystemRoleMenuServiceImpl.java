package com.data.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.SystemMenu;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.exception.GlobalException;
import com.data.service.ISystemRoleMenuService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

@Service
public class SystemRoleMenuServiceImpl extends CommonServiceImpl implements ISystemRoleMenuService {
	
	private static final Logger logger = LoggerFactory.getLogger(SystemRoleMenuServiceImpl.class);

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateRoleMenu(String roleId, String menuIds) {
		if(CommonUtil.isBlank(roleId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "更新角色编号不能为空！");
		}
		if(CommonUtil.isBlank(menuIds)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "更新角色目录编号集合不能为空！");
		}
		String[] ids = CommonUtil.parseIdsCollection(menuIds, ",");
		logger.info("--->>>更新{}角色的{}等权限<<<---", roleId, FastJsonUtil.objectToString(ids));
		delete(DeleteId.DELETE_SYSTEM_ROLE_MENU_BY_ROLE_ID, roleId);
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		for(int i = 0, length = ids.length; i < length; i++) {
			params.put("menuId", ids[i]);
			insert(InsertId.INSERT_NEW_ROLE_MENU, params);
		}
		return ResultUtil.success();
	}

	@Override
	public ResultUtil queryRoleMenu(String roleId) {
		if(CommonUtil.isBlank(roleId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "更新角色编号不能为空！");
		}
		Map<String, Object> params = new HashMap<>(4);
		List<SystemMenu> menuList = queryListByObject(QueryId.QUERY_MENU_LIST, null);
		params.put("menuList", menuList);
		List<String> menuIdList = queryListByObject(QueryId.QUERY_MENU_ID_LIST_BY_ROLE_ID, roleId);
		params.put("menuIdList", menuIdList);
		List<String> parentMenuIdList = queryListByObject(QueryId.QUERY_PARENT_MENU_ID_LIST, null);
		params.put("parentMenuIdList", parentMenuIdList);
		logger.info("--->>>角色{}可以访问的目录为{}<<<---", roleId, FastJsonUtil.objectToString(menuIdList));
		logger.info("--->>>角色{}可以访问的父级目录为{}<<<---", roleId, FastJsonUtil.objectToString(parentMenuIdList));
		return ResultUtil.success(params);
	}

}
