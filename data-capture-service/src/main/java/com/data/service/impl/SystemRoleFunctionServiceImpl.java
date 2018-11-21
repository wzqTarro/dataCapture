package com.data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.SystemRoleFunction;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.enums.CodeEnum;
import com.data.exception.GlobalException;
import com.data.service.ISystemRoleFunctionService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

@Service("systemRoleFunctionService")
public class SystemRoleFunctionServiceImpl extends CommonServiceImpl implements ISystemRoleFunctionService {

	private static final Logger logger = LoggerFactory.getLogger(SystemRoleFunctionServiceImpl.class);

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateRoleFunction(String roleId, String functionIds) {
		logger.info("--->>>更新角色权限的角色为:{} <<<---", roleId);
		if(CommonUtil.isBlank(roleId)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "角色编号不能为空!");
		}
		if(CommonUtil.isNotBlank(functionIds)) {
			String[] idsArray = CommonUtil.parseIdsCollection(functionIds, ",");
			logger.info("--->>>更新的动作权限编码为:{}<<<---", FastJsonUtil.objectToString(idsArray));
			deleteRoleFunction(roleId);
			addRoleFunction(roleId, idsArray);
		}
		return ResultUtil.success("保存成功！");
	}
	
	/**
	 * 添加角色权限
	 * @param roleId
	 * @param ids
	 */
	private final void addRoleFunction(String roleId, String[] idsArray) {
		List<SystemRoleFunction> roleFunctionList = new ArrayList<>(10);
		for(int i = 0, length = idsArray.length; i < length; i++) {
			SystemRoleFunction roleFunction = new SystemRoleFunction();
			roleFunction.setRoleId(roleId);
			roleFunction.setFunctionId(idsArray[i]);
			roleFunctionList.add(roleFunction);
		}
		logger.info("--->>>角色{} 更改权限集合为：{}<<<---", roleId, FastJsonUtil.objectToString(roleFunctionList));
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleFunctionList", roleFunctionList);
		insert(InsertId.BATCH_INSERT_ROLE_FUNCTION, params);
	}
	
	/**
	 * 删除角色权限
	 * @param roleId
	 */
	private final void deleteRoleFunction(String roleId) {
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		delete(DeleteId.DELETE_ROLE_FUNCTION_BY_ROLE_ID, params);
	}

	
}
