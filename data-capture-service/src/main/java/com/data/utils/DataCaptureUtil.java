package com.data.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.data.bean.TemplateSupply;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.QueryId;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.service.impl.CommonServiceImpl;

/**
 * 数据抓取方法工具类
 * @author Alex
 *
 */
public class DataCaptureUtil extends CommonServiceImpl {
	
	private static Logger logger = LoggerFactory.getLogger(DataCaptureUtil.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String getDataByWeb(CommonDTO common, int dataType) {		
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
			throw new DataException("503");
		}
		TemplateSupply supply = (TemplateSupply)queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_ID, common.getId());
		if (false == supply.getIsVal()) {
			throw new DataException("504");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(WebConstant.WEB);
		sb.append(supply.getControllerName());
		sb.append("day1=");
		sb.append(start);
		sb.append("&day2=");
		sb.append(end);
		sb.append("&value=");
		sb.append(dataType);
		logger.info("------>>>>>>抓取数据Url：" + sb.toString() + "<<<<<<--------");
		String saleJson = restTemplate.getForObject(sb.toString(), String.class);
		return saleJson;
	}
	
	@SuppressWarnings("unchecked")
	public <T> PageRecord<T> insertDataByParam(String json, Class<T> clazz, String mapper) throws DataException {
		if (StringUtils.isBlank(json)) {
			logger.info("------>>>>>抓取数据为空<<<<<--------");
			throw new DataException("505");
		}
		List<T> list = (List<T>) FastJsonUtil.jsonToList(json, clazz);
		
		if (null == list) {
			logger.info("----->>>>>>抓取数据转换List为空<<<<<<------");
			throw new DataException("506");
		}
		// 插入到数据库
		insert(mapper, list);
		
		PageRecord<T> page = new PageRecord<>();
		page.setList(list);
		page.setPageTotal(list.size());
		return page;
	}
}
