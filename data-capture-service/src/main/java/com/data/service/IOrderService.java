package com.data.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.data.bean.Order;
import com.data.constant.PageRecord;
import com.data.utils.ResultUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
