package com.data.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.data.bean.Sale;
import com.data.constant.dbSql.QueryId;
import com.data.service.impl.CommonServiceImpl;

/**
 * 库存计算
 * @author Administrator
 *
 */
@Component
public class StockDataCalculateUtil extends CommonServiceImpl{

	/**
	 * 计算库存天数
	 * @param param 查询条件
	 * @param stockPrice 库存金额
	 */
	public double calculateStockDay(Map<String, Object> param, Double stockPrice) {
		// 查询前一天销量
		List<Sale> saleDayList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, param);
	
		// 前一天单品的销售总量
		Integer sumSaleNumByDay = 0;
					
		// 如果前一天存在销售记录，否则默认为0
		if (CommonUtil.isNotBlank(saleDayList)) {
			sumSaleNumByDay = CommonUtil.toIntOrZero(saleDayList.stream().collect(Collectors.summingInt(s -> s.getSellNum())));
		}
					
		// 库存金额
		stockPrice = CommonUtil.toDoubleOrZero(stockPrice);
					
		// 库存天数
		double stockDayNum = 0;
		if (sumSaleNumByDay != 0 && sumSaleNumByDay != null) {
			stockDayNum = CommonUtil.setScale("0.00", stockPrice / sumSaleNumByDay);
		}
		
		return stockDayNum;
		
	}
}
