package com.data.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.Supply;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.UpdateId;
import com.data.service.ISupplyService;
import com.google.common.collect.Maps;

@Service
public class SupplyServiceImpl extends CommonServiceImpl implements ISupplyService {

	private static Logger logger = LoggerFactory.getLogger(SupplyServiceImpl.class);

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public int insert(Supply supply) {
		logger.info("--->>>参数变量需要判断<<<---");
		return insert(InsertId.INSERT_NEW_SUPPLY_MESSAGE, supply);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public int update(Supply supply) {
		return update(UpdateId.UPDATE_SUPPLY_MESSAGE, supply);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public int delete(Integer id) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("id", id);
		return delete(DeleteId.DELETE_SUPPLY_BY_ID, param);
	}

	@Override
	public PageRecord<Supply> queryByConditiion(String pageNum, String pageSize) throws Exception {
		//PageRecord<Supply> page = queryPageByObject(QueryId.QUERY_COUNT_SUPPLY_BY_CONDITION, QueryId.QUERY_SUPPLY_BY_CONDITION, null, pageNum, pageSize);
		return null;
	}
}
