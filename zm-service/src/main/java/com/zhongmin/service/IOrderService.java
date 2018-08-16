package com.zhongmin.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zhongmin.bean.Order;
import com.zhongmin.constant.PageRecord;
import com.zhongmin.util.ResultUtil;

public interface IOrderService {
	/**
	 * 分页查询
	 * @param queryDate
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ResultUtil queryOrderByCondition(String startDate, String endDate, String companyCode, String pageNum, String pageSize) throws Exception ;

}
