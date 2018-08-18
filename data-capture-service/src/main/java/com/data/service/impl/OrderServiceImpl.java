package com.data.service.impl;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.data.bean.Order;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.dto.CommonDTO;
import com.data.service.IOrderService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;

@Service
public class OrderServiceImpl extends CommonServiceImpl implements IOrderService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	
	@Override
	public ResultUtil queryOrderByCondition(String param) throws Exception {
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		Order order = FastJsonUtil.jsonToObject(param, Order.class);
		Map<String, Object> map = Maps.newHashMap();
		if (null != common) {
			if (StringUtils.isNoneBlank(common.getStartDate())) {
				map.put("startDate", common.getStartDate());	
			}
			if (StringUtils.isNoneBlank(common.getEndDate())) {
				map.put("endDate", common.getEndDate());
			}
			}
		if (null != order) {
			if (StringUtils.isNoneBlank(order.getCompanyCode())) {
				map.put("companyCode", order.getCompanyCode());
			}
		}
		PageRecord<Order> page = queryPageByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, QueryId.QUERY_ORDER_BY_CONDITION, map, common.getPage(), common.getLimit());
		return ResultUtil.success(page.getList(), page.getPageTotal());
	}
}
