package com.data.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.data.bean.Supply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.QueryId;
import com.data.dao.ICommonDao;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.service.ICommonService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.google.common.collect.Maps;

public class CommonServiceImpl implements ICommonService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ICommonDao dao;
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public <T> List<T> queryListByObject(String statement, Object parameter) {
		return dao.queryListByObject(statement, parameter);
	}

	@SuppressWarnings("unchecked")
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
	
	@Override
	public String getDataByWeb(String param, int dataType) throws DataException {
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		String start = null;
		String end = null;
		if (null != common && StringUtils.isNoneBlank(common.getStartDate()) && StringUtils.isNoneBlank(common.getEndDate())) {
			start = common.getStartDate();
			end = common.getEndDate();
		} else {
			start = DateUtil.getDate();
			end = DateUtil.getDate();
		}
		if (0 == common.getId()) {
			throw new DataException(TipsEnum.ID_ERROR.getValue());
		}
		Supply supply = (Supply)queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_ID, common.getId());
		StringBuilder sb = new StringBuilder();
		sb.append(WebConstant.WEB);
		sb.append(supply.getUrl());
		sb.append("day1=");
		sb.append(start);
		sb.append("&day2=");
		sb.append(end);
		sb.append("&value=");
		sb.append(WebConstant.SALE);
		logger.info("------>>>>>>抓取数据Url：" + sb.toString() + "<<<<<<--------");
		String saleJson = restTemplate.getForObject(sb.toString(), String.class);
		return saleJson;
	}
}
