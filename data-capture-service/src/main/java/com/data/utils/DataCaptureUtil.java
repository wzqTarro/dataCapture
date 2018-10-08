package com.data.utils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.data.bean.TemplateSupply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.QueryId;
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
	public <T> List<T> getDataByWeb(String queryDate, TemplateSupply supply, int dataType, Class<T> clazz) throws IOException{		
		String start = null;
		String end = null;
		
		if (CommonUtil.isNotBlank(queryDate)) {
			if (!"1900-01-01".equals(queryDate)) {
				start = queryDate.trim();
				end = start;
				
				// 禁止查询当天
				if (DateUtil.format(new Date(), "yyyy-MM-dd").equals(start)) {
					throw new DataException("523");
				}		
			}
		} else {
			throw new DataException("524");
		}		
		if (false == supply.getIsVal()) {
			throw new DataException("504");
		}
		StringBuilder sb = new StringBuilder();
		sb.append(WebConstant.WEB);
		sb.append(supply.getControllerName());
		sb.append("/?");
		sb.append("day1=");
		sb.append(start);
		sb.append("&day2=");
		sb.append(end);
		sb.append("&value=");
		sb.append(dataType);
		logger.info("------>>>>>>抓取数据Url：" + sb.toString() + "<<<<<<--------");
		String json = restTemplate.getForObject(sb.toString(), String.class);
		
		// 测试数据
		// String json = FileUtils.readFileToString(new File("D:\\sale.txt"));
		logger.info("抓取数据：" + json.substring(0, 100));
		// json转List
		List<T> list = translateData(json, clazz);
		logger.info("抓取数据数量:" + list.size());
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
		if (StringUtils.isBlank(json) || "[]".equals(json)) {
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
	public <T> PageRecord<T> setPageRecord(List<T> list, Integer limit) {
		PageRecord<T> pageRecord = new PageRecord<>();			
		pageRecord.setPageNum(1);
		if (null == limit ||  0 == limit ) {
			pageRecord.setPageSize(CommonValue.SIZE);
		} else {
			pageRecord.setPageSize(limit);
		}
		pageRecord.setPageTotal(list.size());
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
		double rowNum = 1000;
		double size = Math.ceil(dataList.size() / rowNum);
		
		if (size == 1) {
			insert(mapper, dataList.subList(0, dataList.size()));
		} else {
			insert(mapper, dataList.subList(0, (int)rowNum));
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		try {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					
					for (int i = 1; i < size; i++) {
						if (i == (size-1)) {
							insert(mapper, dataList.subList(i * (int)rowNum, dataList.size()));
							break;
						}
						insert(mapper, dataList.subList(i * (int)rowNum, i * (int)rowNum + (int)rowNum));
					}
				}
			});
		} finally {
			if (null != executorService) {
				executorService.shutdown();
			}
		}
	}
}
