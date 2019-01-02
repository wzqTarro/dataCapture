package com.data.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.data.bean.Order;
import com.data.bean.Reject;
import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.bean.TemplateSupply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.enums.SupplyEnum;
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
	public String getDataByWeb(String queryDate, TemplateSupply supply, String dataType) throws Exception{		
		
		if (supply == null) {
			throw new Exception("供应链尚未开通");
		}
		
		String start = null;
		String end = null;
		
		if (CommonUtil.isNotBlank(queryDate)) {
			if (!"1900-01-01".equals(queryDate)) {
				start = queryDate.trim();
				end = start;
				
				// 禁止查询当天
				if (Integer.parseInt(DateUtil.format(new Date(), "yyyyMMdd"))<=Integer.valueOf(start.replace("-", ""))) {
					throw new Exception("只可以查询今天之前的数据");
				}		
			}
		} else {
			throw new Exception("查询时间不能为空");
		}		
		if (false == supply.getIsVal()) {
			throw new Exception("供应链尚未开通");
		}
//		// 订单
//		if (WebConstant.ORDER.equals(dataType)) {
//			if (supply.getSysId().equals(SupplyEnum.XSJ.getCode())) {
//				throw new DataException("供应链此功能尚未开通");
//			}
//		}
//		
//		// 退单
//		if (WebConstant.REJECT.equals(dataType)) {
//			if (SupplyEnum.BBG_HUNAN.getCode().equals(supply.getSysId())
//					|| SupplyEnum.BBG_SC_1.getCode().equals(supply.getSysId())
//					|| SupplyEnum.BBG_SC_2.getCode().equals(supply.getSysId())
//					|| supply.getSysId().equals(SupplyEnum.XSJ.getCode())
//					|| supply.getSysId().equals(SupplyEnum.CQ_ZB.getCode())) {
//				throw new DataException("供应链此功能尚未开通");
//			}
//		}
//		
//		// 销售
//		if (WebConstant.SALE.equals(dataType)) {
//			if (SupplyEnum.BBG_HUNAN.getCode().equals(supply.getSysId())
//					|| SupplyEnum.BBG_SC_1.getCode().equals(supply.getSysId())
//					|| SupplyEnum.BBG_SC_2.getCode().equals(supply.getSysId())) {
//				throw new DataException("供应链此功能尚未开通");
//			}
//		}
		StringBuilder sb = new StringBuilder();
		sb.append(WebConstant.WEB);
		sb.append(supply.getControllerName());
		sb.append(dataType);
		sb.append("/?");
		sb.append("day1=");
		sb.append(start);
		sb.append("&day2=");
		sb.append(end);
		sb.append("&user=");
		sb.append(supply.getLoginUserName());
		sb.append("&pwd=");
		sb.append(supply.getLoginPassword());
		sb.append("&venderCode=");
		sb.append(supply.getCompanyCode());
		sb.append("&sysId=");
		sb.append(supply.getSysId());
		//logger.info("------>>>>>>抓取数据Url：" + sb.toString() + "<<<<<<--------");
		String json = restTemplate.getForObject(sb.toString(), String.class);
		
		
		
		// 测试数据
		// String json = FileUtils.readFileToString(new File("D:/sale.txt"));
		// json转List
		// List<T> list = (List<T>) FastJsonUtil.jsonToList(json, clazz);
		// logger.info("抓取数据数量:" + list.size());
 		return json;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		//getSaleExcel("by321");
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
		
		if (!CollectionUtils.isEmpty(list)) {
			pageRecord.setPageTotal(list.size());
			if (list.size() > pageRecord.getPageSize()) {
				pageRecord.setList(list.subList((pageRecord.getPageNum() - 1)*pageRecord.getPageSize(), pageRecord.getPageSize()));
			} else {
				pageRecord.setList(list.subList((pageRecord.getPageNum() - 1)*pageRecord.getPageSize(), list.size()));
			}
		} else {
			pageRecord.setPageTotal(0);
			pageRecord.setList(new ArrayList<T>());
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
