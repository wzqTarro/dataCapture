package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
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
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.OrderEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.service.IOrderService;
import com.data.service.IRedisService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.DateUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.ExportUtil;
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
	
	@Autowired
	private IRedisService redisService;
	
	@Autowired
	private ExportUtil exportUtil;
	
	@Override
	public ResultUtil getOrderByCondition(CommonDTO common, Order order, Integer page, Integer limit) throws Exception {
		logger.info("--->>>订单查询参数common: {}<<<---", FastJsonUtil.objectToString(common));
		logger.info("---->>>order:{}<<<------", FastJsonUtil.objectToString(order));
		Map<String, Object> map = new HashMap<>(8);
		if (null == common) {
			common =  new CommonDTO();
		}
		if (CommonUtil.isNotBlank(common.getStartDate()) && CommonUtil.isNotBlank(common.getEndDate())) {
			map.put("startDate", common.getStartDate());	
			map.put("endDate", common.getEndDate());
		} else {
			String now = DateUtil.format(new Date(), "yyyy-MM-dd");
			map.put("startDate", now);
			map.put("endDate", now);
		}
		logger.info("--------->>>>>>map:{}<<<<<---------", FastJsonUtil.objectToString(map));
		
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
		logger.info("--->>>订单查询结果分页: {}<<<---", FastJsonUtil.objectToString(pageRecord));
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getOrderByWeb(String queryDate, String sysId, Integer limit) {		
		PageRecord<Order> pageRecord = null;
		logger.info("------>>>>>>开始抓取订单数据<<<<<<---------");
		
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		
		Map<String, Object> queryParam = new HashMap<>(2);
		queryParam.put("queryDate", queryDate);
		queryParam.put("sysId", sysId);
		int count = queryCountByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, queryParam);
		
		logger.info("------>>>>>>count:{}<<<<<<-------", count);
		List<Order> orderList = null;
		
		if (count == 0) {
			try {
				orderList = dataCaptureUtil.getDataByWeb(queryDate, sysId, WebConstant.ORDER, Order.class);
			} catch (IOException e) {
				return ResultUtil.error(TipsEnum.GRAB_DATA_ERROR.getValue());
			}
			List<TemplateStore> storeList = redisService.queryTemplateStoreList();
			List<TemplateProduct> productList = redisService.queryTemplateProductList();
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
				
				// 条码信息
				simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
				if (CommonUtil.isBlank(simpleBarCode)) {
					order.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
					continue;
				}
				
				// 单品模板信息
				TemplateProduct product = null;
				String tempSysId = null;
				String tempSimpleBarCode = null;
				for (int j = 0, len = productList.size(); j < len; j++) {
					product = productList.get(j);
					tempSysId = product.getSysId();
					tempSimpleBarCode = product.getSimpleBarCode();
					if (sysId.equals(tempSysId) && simpleBarCode.equals(tempSimpleBarCode)) {
						break;
					}
					product = null;
				}
				if (CommonUtil.isBlank(product)) {
					order.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
					continue;
				}
				// 单品门店信息
				TemplateStore store = null;
				String tempStoreCode = null;
				String tempOrderStoreName = null;
				for (int j = 0, len = storeList.size(); j < len; j++) {
					store = storeList.get(j);
					tempSysId = store.getSysId();
					tempStoreCode = store.getStoreCode();
					tempOrderStoreName = store.getOrderStoreName() == null ? "" : store.getOrderStoreName();
					if (sysId.equals(tempSysId) && (storeCode.equals(tempStoreCode) || tempOrderStoreName.contains(storeCode))) {
						break;
					}
					store = null;
				}
				if (null == store) {
					order.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
					continue;
				}
				// 大区
				order.setRegion(store.getRegion());
					
				// 省区
				order.setProvinceArea(store.getProvinceArea());
					
				// 门店名称
				order.setStoreName(store.getStandardStoreName());
				
				// 单品条码
				order.setSimpleBarCode(simpleBarCode);
				
				// 单品箱规
				order.setBoxStandard(product.getBoxStandard());
				
				// 单品名称
				order.setSimpleName(product.getStandardName());
					
				// 箱规
				order.setBoxStandard(product.getBoxStandard());
					
				// 库存编号
				order.setStockCode(product.getStockCode());
			}
			// 插入数据
			logger.info("----->>>>>>开始插入订单数据<<<<<<------");
			dataCaptureUtil.insertData(orderList, InsertId.INSERT_BATCH_ORDER);
		} else {
			orderList = queryListByObject(QueryId.QUERY_ORDER_BY_CONDITION, queryParam);
		}
		pageRecord = dataCaptureUtil.setPageRecord(orderList, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public void exportOrderExcel(String stockNameStr, CommonDTO common, Order order, OutputStream output) throws Exception {
		logger.info("----->>>>自定义字段：{}<<<<------", stockNameStr);
		logger.info("----->>>>common：{}<<<<------", FastJsonUtil.objectToString(common));
		logger.info("----->>>>order：{}<<<<------", FastJsonUtil.objectToString(order));
		exportUtil.exportConditionJudge(common, order, stockNameStr);
		String[] header = CommonUtil.parseIdsCollection(stockNameStr, ",");
		Map<String, Object> map = exportUtil.joinColumn(OrderEnum.class, header);
		
		// 调用方法名
		String[] methodNameArray = (String[]) map.get("methodNameArray");
		
		// 导出字段
		String column = (String) map.get("column");
		
		// 自选导出excel表查询字段
		Map<String, Object> param = exportUtil.joinParam(common.getStartDate(), common.getEndDate(), column, order.getSysId());
		
		// 门店编号
		if (CommonUtil.isNotBlank(order.getStoreCode())) {
			param.put("storeCode", order.getStoreCode());
		}
		
		// 单据号码
		if (CommonUtil.isNotBlank(order.getReceiptCode())) {
			param.put("receiptCode", order.getReceiptCode());
		}
		List<Order> dataList = queryListByObject(QueryId.QUERY_ORDER_BY_ANY_COLUMN, param);
		
		ExcelUtil<Order> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007("订单信息", header, methodNameArray, dataList, output);
	}

}
