package com.data.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.dao.ICommonDao;
import com.data.service.ICommonService;
import com.data.utils.FastJsonUtil;
import com.google.common.collect.Maps;

public class CommonServiceImpl implements ICommonService{
	@Autowired
	private ICommonDao dao;

	@Override
	public <T> List<T> queryListByObject(String statement, Object parameter) {
		return dao.queryListByObject(statement, parameter);
	}

	@Override
	public <T> PageRecord<T> queryPageByObject(String countStatement, String listStatement, Object parameter,
			Integer pageNum, Integer pageSize) throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		if (parameter != null) {
			params = FastJsonUtil.jsonToObject(FastJsonUtil.objectToString(parameter), Map.class);		
		}
		if (null == pageNum || "0".equals(pageNum)) {
			pageNum = CommonValue.PAGE;
		}
		if (null == pageSize || "0".equals(pageSize)) {
			pageSize = CommonValue.SIZE;
		}
		/**
		 * TODO
		 * 报空指针异常 需处理
		 */
		int num = (pageNum - 1) * pageSize;
		int size = pageSize;
		params.put("pageNum", num);
		params.put("pageSize", size);
		PageRecord<T> page = new PageRecord<>();
		page.setList(dao.queryListByObject(listStatement, params));
		page.setPageTotal(dao.queryCountByObject(countStatement, params));
		page.setPageNum(num);
		page.setPageSize(size);
		return page;
	}

	@Override
	public int queryCountByObject(String statement, Object parameter) {
		return dao.queryCountByObject(statement, parameter);
	}

	@Override
	public int insert(String statement, Object parameter) {
		return dao.insert(statement, parameter);
	}

	@Override
	public int update(String statement, Object parameter) {
		return dao.update(statement, parameter);
	}

	@Override
	public int delete(String statement, Object parameter) {
		return dao.delete(statement, parameter);
	}

	@Override
	public Object queryObjectByParameter(String statement, Object parameter) {
		return dao.queryObjectByParameter(statement, parameter);
	}
}
