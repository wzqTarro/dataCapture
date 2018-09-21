package com.data.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.data.bean.SimpleCode;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.SimpleCodeEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.service.impl.CommonServiceImpl;

/**
 * 数据抓取方法工具类
 * @author Alex
 *
 */
@Component
public class DataCaptureUtil extends CommonServiceImpl {
	
	private static Logger logger = LoggerFactory.getLogger(DataCaptureUtil.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 获取python抓取的数据
	 * @param common
	 * @param dataType
	 * @return
	 * @throws IOException 
	 */
	public <T> List<T> getDataByWeb(CommonDTO common, int sysId, int dataType, Class<T> clazz) throws IOException{		
		/*String start = null;
		String end = null;
		if (0 == sysId) {
			throw new DataException("503");
		}
		if (null != common && StringUtils.isNoneBlank(common.getStartDate()) && StringUtils.isNoneBlank(common.getEndDate())) {
			start = common.getStartDate();
			end = common.getEndDate();
		} else {
			start = DateUtil.getDate();
			end = start;
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
		String json = restTemplate.getForObject(sb.toString(), String.class);*/
		
		// 测试数据
		String json = FileUtils.readFileToString(new File("E:\\baiya\\sale\\sale.txt"));
		logger.info(json);
		// json转List
		List<T> list = translateData(json, clazz);
		
 		return list;
	}
	/**
	 * 转化json
	 * @param json
	 * @param clazz
	 * @return
	 * @throws DataException
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> translateData(String json, Class<T> clazz){		
		if (StringUtils.isBlank(json)) {
			logger.info("------>>>>>抓取数据为空<<<<<--------");
			throw new DataException("505");
		}
		List<T> list = (List<T>) FastJsonUtil.jsonToList(json, clazz);		
		if (CommonUtil.isBlank(list)) {
			logger.info("----->>>>>>抓取数据转换List为空<<<<<<------");
			throw new DataException("506");
		}
		return list;
	}
	/**
	 * 设置分页
	 * @param list
	 * @param common
	 * @return
	 */
	public <T> PageRecord<T> setPageRecord(List<T> list, Integer page, Integer limit) {
		PageRecord<T> pageRecord = new PageRecord();
		if (null == page || 0 == page) {
			pageRecord.setPageNum(CommonValue.PAGE);			
		} else {			
			pageRecord.setPageNum(page);
		}
		if (null == limit ||  0 == limit ) {
			pageRecord.setPageSize(CommonValue.SIZE);
		} else {
			pageRecord.setPageSize(limit);
		}
		if (list.size() > pageRecord.getPageSize()) {
			pageRecord.setList(list.subList((pageRecord.getPageNum() - 1)*pageRecord.getPageSize(), pageRecord.getPageSize()));
		} else {
			pageRecord.setList(list.subList((pageRecord.getPageNum() - 1)*pageRecord.getPageSize(), list.size()));
		}
	
		return pageRecord;
	}
	/**
	 * 批量插入数据库
	 * @param dataList
	 * @param clazz
	 * @param mapper
	 * @return
	 */
	public <T> void insertData(List<T> dataList, String mapper) {
		// 插入到数据库
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			executorService.execute(new Runnable() {	
				@Override
				public void run() {
					insert(mapper, dataList);
				}
			});
		} finally {
			if (null != executorService) {
				executorService.shutdown();
			}
		}
	}
}
