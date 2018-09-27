package com.data.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.Order;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.service.IOrderService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.data.utils.TemplateDataUtil;

@Service
public class OrderServiceImpl extends CommonServiceImpl implements IOrderService {
	
	private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired
	private DataCaptureUtil dataCaptureUtil;
	
	@Autowired
	private TemplateDataUtil templateDataUtil;
	
	@Override
	public ResultUtil getOrderByCondition(CommonDTO common, Order order, Integer page, Integer limit) throws Exception {
		logger.info("--->>>订单查询参数common: {}<<<---", FastJsonUtil.objectToString(common));
		logger.info("---->>>order:{}<<<------", FastJsonUtil.objectToString(order));
		Map<String, Object> map = new HashMap<>(8);
		if (null != common) {
			if (CommonUtil.isNotBlank(common.getStartDate())) {
				map.put("startDate", common.getStartDate());	
			}
			if (CommonUtil.isNotBlank(common.getEndDate())) {
				map.put("endDate", common.getEndDate());
			}
		}
		if (null != order) {
			if (CommonUtil.isNotBlank(order.getSysId())) {
				map.put("sysId", order.getSysId());
			}
			if (CommonUtil.isNotBlank(order.getSimpleBarCode())) {
				map.put("simpleBarCode", order.getSimpleBarCode());
			}
			if (CommonUtil.isNotBlank(order.getStoreCode())) {
				map.put("storeCode", order.getStockCode());
			}
			if (CommonUtil.isNotBlank(order.getRegion())) {
				map.put("region", order.getRegion());
			}
			if (CommonUtil.isNotBlank(order.getProvinceArea())) {
				map.put("provinceArea", order.getProvinceArea());
			}
			if (CommonUtil.isNotBlank(order.getReceiptCode())) {
				map.put("receiptCode", order.getReceiptCode());
			}
		}
		PageRecord<Order> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, QueryId.QUERY_ORDER_BY_CONDITION, map, page, limit);
		logger.info("--->>>订单查询结果分页: {}<<<---", FastJsonUtil.objectToString(page));
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getOrderByWeb(String queryDate, String sysId, Integer page, Integer limit){
		List<Order> orderList = null;
		try {
			orderList = dataCaptureUtil.getDataByWeb(queryDate, sysId, WebConstant.ORDER, Order.class);
		} catch (IOException e) {
			return ResultUtil.error(TipsEnum.GRAB_DATA_ERROR.getValue());
		}
		for (int i = 0, size = orderList.size(); i < size; i++) {
			Order order = orderList.get(i);
			
			// 系统名称
			String sysName = order.getSysName();
			
			// 单品编码
			String simpleCode = order.getSimpleCode();
			
			// 单品条码
			String simpleBarCode = order.getSimpleBarCode();
			
			// 门店编号
			String storeCode = order.getStoreCode();
			
			// 模板门店信息
			TemplateStore store = null;
			try {
				store = templateDataUtil.getStandardStoreMessage(sysId, storeCode);
			} catch (Exception e) {
				order.setRemark("模板门店信息获取失败");
				continue;
			}
			if (null != store) {
				order.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				continue;
			}
			
			// 条码信息
			simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
			if (CommonUtil.isBlank(simpleBarCode)) {
				order.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
				continue;
			}
			
			// 模板商品信息
			TemplateProduct product = templateDataUtil.getStandardProductMessage(sysId, simpleBarCode);
			if (CommonUtil.isBlank(product)) {
				order.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				continue;
			}
			order.setSimpleBarCode(simpleBarCode);
			order.setBoxStandard(product.getBoxStandard());
			order.setProvinceArea(product.getProductId());
		}
		return null;
	}

}
