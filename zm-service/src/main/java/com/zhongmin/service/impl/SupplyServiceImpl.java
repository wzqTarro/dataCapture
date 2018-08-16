package com.zhongmin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.zhongmin.bean.Supply;
import com.zhongmin.constant.PageRecord;
import com.zhongmin.constant.dbSql.DeleteId;
import com.zhongmin.constant.dbSql.InsertId;
import com.zhongmin.constant.dbSql.QueryId;
import com.zhongmin.constant.dbSql.UpdateId;
import com.zhongmin.service.ISupplyService;

@Service
public class SupplyServiceImpl extends CommonServiceImpl implements ISupplyService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public int insert(Supply supply) {
		return insert(InsertId.INSERT_NEW_SUPPLY_MESSAGE, supply);
	}

	@Override
	public int update(Supply supply) {
		return update(UpdateId.UPDATE_SUPPLY_MESSAGE, supply);
	}

	@Override
	public int delete(Integer id) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("id", id);
		return delete(DeleteId.DELETE_SUPPLY_BY_ID, param);
	}

	@Override
	public PageRecord<Supply> queryByConditiion(String pageNum, String pageSize) throws Exception {
		PageRecord<Supply> page = queryPageByObject(QueryId.QUERY_COUNT_SUPPLY_BY_CONDITION, QueryId.QUERY_SUPPLY_BY_CONDITION, null, pageNum, pageSize);
		return page;
	}
	
	
}
