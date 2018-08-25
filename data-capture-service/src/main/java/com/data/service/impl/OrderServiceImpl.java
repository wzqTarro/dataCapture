package com.data.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.data.bean.Order;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.QueryId;
import com.data.dto.CommonDTO;
import com.data.service.IOrderService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.google.common.collect.Maps;

@Service
public class OrderServiceImpl extends CommonServiceImpl implements IOrderService {
	
	private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Override
	public ResultUtil queryOrderByCondition(String param) throws Exception {
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		Order order = FastJsonUtil.jsonToObject(param, Order.class);
		logger.info("--->>>订单查询参数: {}<<<---", param);
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
		logger.info("--->>>订单查询结果分页: {}<<<---", FastJsonUtil.objectToString(page));
		return ResultUtil.success(page);
	}
}
