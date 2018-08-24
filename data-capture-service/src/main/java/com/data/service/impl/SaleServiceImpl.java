package com.data.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.data.bean.Sale;
import com.data.bean.Supply;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.service.ISaleService;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.google.common.collect.Maps;

@Service
public class SaleServiceImpl extends CommonServiceImpl implements ISaleService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ResultUtil getSaleByWeb(String param) {
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		String saleJson = null;
		PageRecord<Sale> page = null;
		try {
			logger.info("------>>>>>>开始抓取销售数据<<<<<<---------");
			
			// 抓取数据
			saleJson = getDataByWeb(common, WebConstant.SALE);
			logger.info("------>>>>>>>抓取到的销售数据：" + saleJson + "<<<<<<<<--------");
			
			// 数据插入数据库
			page = insertDataByParam(saleJson, Sale.class, InsertId.INSERT_BATCH_SALE);
			logger.info("------>>>>>>结束抓取销售数据<<<<<<---------");
		} catch (DataException e) {
			return ResultUtil.error(e.getMessage());
		}
		return ResultUtil.success(page);
	}

	@Override
	public ResultUtil getSaleByParam(String param) throws Exception {
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		Sale sale = FastJsonUtil.jsonToObject(param, Sale.class);
		if (null == common) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		Map<String, Object> map = Maps.newHashMap();
		logger.info("--------->>>>>>>>common:" + FastJsonUtil.objectToString(common) + "<<<<<<<-----------");		
		if (StringUtils.isNoneBlank(common.getStartDate()) && StringUtils.isNoneBlank(common.getEndDate())) {
			map.put("startDate", common.getStartDate());
			map.put("endDate", common.getEndDate());
		} else {
			// 默认查询当天数据
			map.put("startDate", DateUtil.getDate());
			map.put("endDate", DateUtil.getDate());
		}
		logger.info("--------->>>>>>>>sale:" + FastJsonUtil.objectToString(sale) + "<<<<<<<<----------");
		if (null != sale) {
			
			// 门店名称
			if (StringUtils.isNoneBlank(sale.getStoreName())) {
				map.put("storeName", sale.getStoreName());
			}
			 
			// 区域
			if (StringUtils.isNoneBlank(sale.getRegion())) {
				map.put("region", sale.getRegion());
			}
			
			// 系列
			if (StringUtils.isNoneBlank(sale.getSeries())) {
				map.put("series", sale.getSeries());
			}
			
			// 单品名称
			if (StringUtils.isNoneBlank(sale.getSimpleName())) {
				map.put("simpleName", sale.getSimpleName());
			}
		}
		PageRecord<Sale> page = queryPageByObject(QueryId.QUERY_COUNT_SALE_BY_PARAM, QueryId.QUERY_SALE_BY_PARAM, map, common.getPage(), common.getLimit());
		return ResultUtil.success(page);
	}
}
