package com.data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.data.bean.SystemFunction;
import com.data.constant.dbSql.QueryId;
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
		result.setData(FastJsonUtil.objectToString(functionList));
		return result;
	}
	
}
