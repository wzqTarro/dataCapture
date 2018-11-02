package com.data.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.data.bean.SystemFunction;
import com.data.constant.dbSql.QueryId;
import com.data.service.ISystemFunctionService;

@Service("systemFunctionService")
public class SystemFunctionServiceImpl extends CommonServiceImpl implements ISystemFunctionService {

	private static final Logger logger = LoggerFactory.getLogger(SystemFunctionServiceImpl.class);
	
	@Override
	public List<SystemFunction> queryFunctionList(String roleId) {
		Map<String, Object> params = new HashMap<>(4);
		params.put("roleId", roleId);
		return queryListByObject(QueryId.QUERY_FUNCTION_BY_ROLE_ID, params);
	}
	
}
