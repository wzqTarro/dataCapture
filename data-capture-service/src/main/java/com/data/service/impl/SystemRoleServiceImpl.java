package com.data.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.SystemRole;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.CodeEnum;
import com.data.exception.GlobalException;
import com.data.service.ISystemRoleService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

@Service("systemRoleSerivce")
public class SystemRoleServiceImpl extends CommonServiceImpl implements ISystemRoleService {

	private static final Logger logger = LoggerFactory.getLogger(SystemRoleServiceImpl.class);

	@Override
	public ResultUtil querySystemRoleByPage(String page, String limit) throws Exception {
		if(CommonUtil.isBlank(page)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "页码不能为空！");
		}
		if(CommonUtil.isBlank(limit)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "页面内容条数不能为空！");
		}
		PageRecord<SystemRole> rolePage = queryPageByObject(QueryId.QUERY_COUNT_ROLE_BY_PAGE,
				QueryId.QUERY_ROLE_LIST_BY_PAGE, null, page, limit);
		logger.info("--->>>角色分页集合为: {}<<<---", FastJsonUtil.objectToString(rolePage));
		return ResultUtil.success(rolePage);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil addSystemRole(SystemRole role) {
		Map<String, Object> params = validateRoleParameter(role);
		logger.info("--->>>添加角色参数为: {}<<<---", FastJsonUtil.objectToString(params));
		int count = insert(InsertId.INSERT_ADD_NEW_ROLE, params);
		return ResultUtil.success(count);
	}
	
	private final Map<String, Object> validateRoleParameter(SystemRole role) {
		if(CommonUtil.isBlank(role)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "传入对象参数有误!");
		}
		Map<String, Object> params = new HashMap<>(10);
		String roleId = role.getRoleId();
		if(CommonUtil.isBlank(roleId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色ID不能为空!");
		}
		params.put("roleId", roleId);
		int count = queryCountByObject(QueryId.QUERY_COUNT_ROLE_BY_ROLE_ID, params);
		if(count == 0) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "该角色编号已使用，请更换！");
		}
		if(CommonUtil.isOverLength(roleId, 16)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色编号长度不能超过16位！");
		}
		String roleName = role.getRoleName();
		if(CommonUtil.isBlank(roleName)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色名称不能为空！");
		}
		if(CommonUtil.isOverLength(roleName, 16)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色名称长度不能超过16位！");
		}
		params.put("roleName", roleName);
		params.put("isEnable", role.getIsEnable());
		params.put("isDelete", CodeEnum.CODE_VALUE_00_ENUM.value());
		return params;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateSystemRole(SystemRole role) {
		Map<String, Object> params = new HashMap<>(10);
		String roleId = role.getRoleId();
		if(CommonUtil.isOverLength(roleId, 16)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色编号长度不能超过16位！");
		}
		String roleName = role.getRoleName();
		if(CommonUtil.isOverLength(roleName, 16)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色名称长度不能超过16位！");
		}
		if(CommonUtil.isNotBlank(roleId)) {
			params.put("roleId", roleId);
		}
		if(CommonUtil.isNotBlank(roleName)) {
			params.put("roleName", roleName);
		}
		if(CommonUtil.isNotBlank(role.getIsEnable())) {
			params.put("isEnable", role.getIsEnable());
		}
		logger.info("--->>>更新角色参数为: {}<<<---", FastJsonUtil.objectToString(params));
		int count = update(UpdateId.UPDATE_ROLE_BY_ROLE_ID, params);
		return ResultUtil.success(count);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil deleteSystemRoleByRoleId(String roleId) {
		if(CommonUtil.isBlank(roleId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色编号不能为空！");
		}
		Map<String, Object> params = new HashMap<>(10);
		params.put("roleId", roleId);
		params.put("isDelete", CodeEnum.CODE_VALUE_01_ENUM.value());
		params.put("isEnable", CodeEnum.CODE_VALUE_01_ENUM.value());
		int count = update(UpdateId.UPDATE_ROLE_BY_ROLE_ID, params);
		return ResultUtil.success(count);
	}

	@Override
	public ResultUtil querySystemRoleDetail(String roleId) {
		if(CommonUtil.isBlank(roleId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色编号不能为空！");
		}
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		SystemRole role = (SystemRole) queryObjectByParameter(QueryId.QUERY_ROLE_BY_ROLE_ID, params);
		logger.info("--->>>编号为{}的角色的详细信息为{}<<<---", roleId, role);
		return ResultUtil.success(role);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil deleteSystemRoleByIds(String roleIds) {
		logger.info("--->>>批量删除角色编号{}<<<---", roleIds);
		String[] roleIdArray = null;
		Map<String, Object> params = new HashMap<>(4);
		params.put("isDelete", CodeEnum.CODE_VALUE_01_ENUM.value());
		params.put("isEnable", CodeEnum.CODE_VALUE_01_ENUM.value());
		if(CommonUtil.isNotBlank(roleIds)) {
			roleIdArray = CommonUtil.parseIdsCollection(roleIds, ",");
			for(int i = 0, length = roleIdArray.length; i < length; i++) {
				params.put("roleId", roleIdArray[i]);
				update(UpdateId.UPDATE_ROLE_BY_ROLE_ID, params);
			}
			return ResultUtil.success();
		}
		return null;
	}
}
