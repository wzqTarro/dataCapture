package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.data.bean.DataLog;
import com.data.bean.Order;
import com.data.bean.PromotionDetail;
import com.data.bean.PromotionStoreList;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.constant.enums.ExcelEnum;
import com.data.constant.enums.OrderEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.exception.GlobalException;
import com.data.model.OrderModel;
import com.data.service.ICodeDictService;
import com.data.service.IDataService;
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
	
	@Autowired
	private ICodeDictService codeDictService;
	
	@Autowired
	private IDataService dataService;
	
	public static void main(String[] args) {
		List<Order> list = new ArrayList<>();
		Order o1 = new Order();
		Order o2 = new Order();
		o1.setId(1);
		o1.setSysId("1");
		o2.setSysId("2");
		o2.setId(2);
		list.add(o1);
		list.add(o2);
		for (int i = 0; i < list.size(); i++){
			Order o = list.get(i);
			o.setId(i);
		}
		System.err.println(JSON.toJSONString(list));
		
	}
	@Override
	public ResultUtil getOrderByCondition(CommonDTO common, Order order, Integer page, Integer limit) throws Exception {
		logger.info("--->>>订单查询参数common: {}<<<---", FastJsonUtil.objectToString(common));
		logger.info("---->>>订单多条件查询前台返回order:{}<<<------", FastJsonUtil.objectToString(order));
		Map<String, Object> map = new HashMap<>(8);
		if (null == common) {
			common =  new CommonDTO();
		}
		if (CommonUtil.isNotBlank(common.getStartDate()) && CommonUtil.isNotBlank(common.getEndDate())) {
			map.put("startDate", common.getStartDate());	
			map.put("endDate", common.getEndDate());
		} else {
			throw new DataException("534");
		}
		
		if (null != order) {
			if (CommonUtil.isNotBlank(order.getSysId())) {
				map.put("sysId", order.getSysId());
			}
			if (CommonUtil.isNotBlank(order.getSimpleBarCode())) {
				map.put("simpleBarCode", order.getSimpleBarCode());
			}
			if (CommonUtil.isNotBlank(order.getStoreCode())) {
				map.put("storeCode", order.getStoreCode());
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
		logger.info("--------->>>>>>订单多条件查询参数map:{}<<<<<---------", FastJsonUtil.objectToString(map));
		PageRecord<Order> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, QueryId.QUERY_ORDER_BY_CONDITION, map, page, limit);
		logger.info("--->>>订单查询结果分页: {}<<<---", FastJsonUtil.objectToString(pageRecord));
		return ResultUtil.success(pageRecord);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultUtil getOrderByWeb(String queryDate, Integer id, Integer limit) throws Exception {		
		PageRecord<Order> pageRecord = null;
		logger.info("------>>>>>>开始抓取订单数据<<<<<<---------");		
		logger.info("------>>>>>>系统id:{},查询时间queryDate:{}<<<<<<<-------", id, queryDate);
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		if (id == null || id == 0) {
			throw new Exception("id不能为空");
		}
		
		// 同步
		synchronized(id) {
			logger.info("------>>>>>进入抓取订单同步代码块<<<<<-------");
			Map<String, Object> queryParam = new HashMap<>(2);
			queryParam.put("id", id);	
			TemplateSupply supply = (TemplateSupply)queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_CONDITION, queryParam);
			if (supply == null) {
				return ResultUtil.error("供应链未找到");
			}
			String sysId = supply.getSysId();
			
			queryParam.clear();
			queryParam.put("queryDate", queryDate);
			queryParam.put("sysId", sysId);
			int count = queryCountByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, queryParam);
			
			logger.info("------>>>>>>原数据库中订单数据数量count:{}<<<<<<-------", count);
			List<Order> orderList = null;
			
			String orderStr = null;
			
			if (count == 0) {
				
				//boolean flag = true;
				
/*				while (flag) {
					try {*/
						orderStr = dataCaptureUtil.getDataByWeb(queryDate, supply, WebConstant.ORDER);
/*						if (orderStr != null) {
							flag = false;
							logger.info("----->>>>抓取订单数据结束<<<<------");
						}
					} catch (DataException e) {
						return ResultUtil.error(e.getMessage());
					} catch (Exception e) {
						flag = true;
					}
				}*/
				if (orderStr == null) {
					return ResultUtil.error("抓取失败");	
				}else {
					logger.info("----->>>>抓取订单数据结束<<<<------");
				}
	
				orderList = (List<Order>) FastJsonUtil.jsonToList(orderStr, Order.class);
				
				if (CollectionUtils.isEmpty(orderList)) {
					pageRecord = dataCaptureUtil.setPageRecord(orderList, limit);
					return ResultUtil.success(pageRecord);
				}
				
				// 匹配数据
				mateData(queryDate, supply, orderList);
				
				// 插入数据
				logger.info("----->>>>>>开始插入订单数据<<<<<<------");
				insert(InsertId.INSERT_ORDER_BATCH_NEW, orderList);
				// dataCaptureUtil.insertData(orderList, InsertId.INSERT_BATCH_ORDER);
			} else {
				orderList = queryListByObject(QueryId.QUERY_ORDER_BY_CONDITION, queryParam);
			}
			
			pageRecord = dataCaptureUtil.setPageRecord(orderList, limit);
		}
		return ResultUtil.success(pageRecord);
	}
	
	/**
	 * 匹配数据
	 * @param queryDate
	 * @param supply
	 * @param orderList
	 * @throws Exception
	 */
	private void mateData(String queryDate, TemplateSupply supply, List<Order> orderList) throws Exception {
		List<TemplateStore> storeList = redisService.queryTemplateStoreList();
		List<TemplateProduct> productList = redisService.queryTemplateProductList();
		
		String sysId = supply.getSysId();
		
		Order order = null;
		
		// 查询促销明细
		Map<String, Object> param = new HashMap<>(2);
		param.put("sysId", sysId);
		param.put("queryDate", queryDate);
		List<PromotionDetail> promotionList = queryListByObject(QueryId.QUERY_PROMOTION_DETAIL_BY_PARAM, param);
		
		PromotionDetail promotionDetail = null;
		
		// 入库方式
		String supplyOrderType = null;
		
		// 含税进价
		BigDecimal buyPriceWithTax = null;
		
		// 促销供价
		BigDecimal supplyPrice = null;
		
		// 含税合同供价
		BigDecimal contractPrice = null;
		for (int i = 0, size = orderList.size(); i < size; i++) {
			order = orderList.get(i);
			
			String region = supply.getRegion();
			
			// 系统名称
			String sysName = supply.getSysName();
			
			// 单品编码
			String simpleCode = order.getSimpleCode();
			
			// 单品条码
			String simpleBarCode = order.getSimpleBarCode();
			
			// 门店编号
			String storeCode = order.getStoreCode();
			
			order.setSysId(sysId);
			order.setSysName(supply.getRegion() + sysName);
			order.setStatus(1);
			
			// 条码信息
			if (StringUtils.isBlank(simpleBarCode)) {
				try {
					simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
					if (CommonUtil.isBlank(simpleBarCode)) {
						
						// 错误日志
						DataLog log = new DataLog();
						log.setRegion(region);
						log.setLogDate(DateUtil.stringToDate(queryDate));
						log.setSysId(sysId);
						log.setSysName(sysName);
						log.setLogRemark("编码"+simpleCode+"商品"+TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						insert(InsertId.INSERT_DATA_LOG, log);
						order.setStatus(0);
						//order.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						continue;
					}
				} catch (GlobalException e) {
					// 错误日志
					DataLog log = new DataLog();
					log.setRegion(region);
					log.setLogDate(DateUtil.stringToDate(queryDate));
					log.setSysId(sysId);
					log.setSysName(sysName);
					log.setLogRemark(e.getErrorMsg());
					insert(InsertId.INSERT_DATA_LOG, log);
					order.setStatus(0);
					continue;
				}
				
			}
			
			// 单品条码
			order.setSimpleBarCode(simpleBarCode);
			
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
				
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(DateUtil.stringToDate(queryDate));
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码"+simpleBarCode+"商品"+TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				order.setStatus(0);
				//order.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				//continue;
			} else {
				
				// 单品箱规
				order.setBoxStandard(product.getBoxStandard());
				
				// 单品名称
				order.setSimpleName(product.getStandardName());
					
				// 箱规
				order.setBoxStandard(product.getBoxStandard());
					
				// 库存编号
				order.setStockCode(product.getStockCode());
				
				// 含税合同供价
				contractPrice = product.getIncludeTaxPrice();
				order.setContractPrice(contractPrice);
				
				// 含税进价
				buyPriceWithTax = order.getBuyingPriceWithRate();
				
				int j = 0;
				int promotionSize = 0;
				for (j = 0, promotionSize = promotionList.size(); j < promotionSize; j++) {
					promotionDetail = promotionList.get(j);
					Integer detailId = promotionDetail.getId();
					List<PromotionStoreList> promotionStoreList = queryListByObject(QueryId.QUERY_PROMOTION_STORE_LIST_BY_DETAIL_ID, detailId);
					
					if (JSONPath.eval(promotionStoreList, "$[storeCode='"+ storeCode +"']") != null) {
						if (simpleBarCode.equals(promotionDetail.getProductCode())) {
							order.setOrderEffectiveJudge("是促销期内订单");
							// 促销供价开始、结束时间
							order.setDiscountStartDate(promotionDetail.getSupplyPriceStartDate());
							order.setDiscountEndDate(promotionDetail.getSupplyPriceEndDate());
							
							// 补差方式
							order.setBalanceWay(promotionDetail.getCompensationType());
							
							// 供价方式
							supplyOrderType = promotionDetail.getSupplyOrderType();
							
							if ("特供价入库".equals(supplyOrderType)) {				
								
								// 促销供价
								supplyPrice = promotionDetail.getSupplyPrice();
								order.setDiscountPrice(supplyPrice);
								
								// 促销供价差异
								order.setDiffPriceDiscount(buyPriceWithTax.subtract(supplyPrice));
								
								// 促销供价差异汇总
								order.setDiffPriceDiscountTotal(order.getDiffPriceDiscount().multiply(new BigDecimal(order.getSimpleAmount())));
								
								// 供价示警
								if (buyPriceWithTax.compareTo(supplyPrice) < 0) {
									order.setDiscountAlarmFlag("订单低于促销供价");
								}
								
							} else if ("原价入库".equals(supplyOrderType)) {
								
								// 合同供价差异
								order.setDiffPriceContract(buyPriceWithTax.subtract(contractPrice));
								
								// 合同供价差异汇总
								order.setDiffPriceContractTotal(order.getDiffPriceContract().multiply(new BigDecimal(order.getSimpleAmount())));
								
								if (buyPriceWithTax.compareTo(contractPrice) < 0) {
									order.setContractAlarmFlag("订单低于合同供价");
								}
							}
							break;
							
						}
					}
				}
				
				// 不处于促销范围之内
				if (j == promotionSize) {
					order.setOrderEffectiveJudge("非促销期内订单");
					// 含税合同供价
					contractPrice = product.getIncludeTaxPrice();
					order.setContractPrice(contractPrice);
					
					// 合同供价差异
					order.setDiffPriceContract(buyPriceWithTax.subtract(contractPrice));
					
					// 合同供价差异汇总
					order.setDiffPriceContractTotal(order.getDiffPriceContract().multiply(new BigDecimal(order.getSimpleAmount())));
					
					if (buyPriceWithTax.compareTo(contractPrice) < 0) {
						order.setContractAlarmFlag("订单低于合同供价");
					}
				}
				
				// 汇总差异
				order.setDiffPrice(order.getDiffPriceContractTotal().add(order.getDiffPriceDiscountTotal()==null?new BigDecimal(0):order.getDiffPriceDiscountTotal()));
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
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(DateUtil.stringToDate(queryDate));
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码"+simpleBarCode+"商品"+TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				order.setStatus(0);
				//order.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				//continue;
			} else {
				// 大区
				order.setRegion(store.getRegion());
					
				// 省区
				order.setProvinceArea(store.getProvinceArea());
					
				// 门店名称
				order.setStoreName(store.getStandardStoreName());
			}
			
		}
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
		
		if (CommonUtil.isNotBlank(order.getSysId())) {
			param.put("sysId", order.getSysId());
		}
		if (CommonUtil.isNotBlank(order.getSimpleBarCode())) {
			param.put("simpleBarCode", order.getSimpleBarCode());
		}
		if (CommonUtil.isNotBlank(order.getStoreCode())) {
			param.put("storeCode", order.getStockCode());
		}
		if (CommonUtil.isNotBlank(order.getRegion())) {
			param.put("region", order.getRegion());
		}
		if (CommonUtil.isNotBlank(order.getProvinceArea())) {
			param.put("provinceArea", order.getProvinceArea());
		}
		if (CommonUtil.isNotBlank(order.getReceiptCode())) {
			param.put("receiptCode", order.getReceiptCode());
		}
		logger.info("------->>>>>导出条件：{}<<<<<-------", FastJsonUtil.objectToString(param));
		List<Order> dataList = queryListByObject(QueryId.QUERY_ORDER_BY_ANY_COLUMN, param);
		ExcelUtil<Order> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007("订单信息", header, methodNameArray, dataList, output);
	}

	@Override
	public ResultUtil queryOrderAlarmList(CommonDTO common, OrderModel order, Integer page, Integer limit) throws Exception {
		Map<String, Object> params = buildQueryParamsMap(order);
		if (common == null || StringUtils.isBlank(common.getStartDate()) || StringUtils.isBlank(common.getEndDate())) {
			throw new DataException("534");
		}
		params.put("startDate", common.getStartDate());
		params.put("endDate", common.getEndDate());
		logger.info("--->>>订单报警列表查询参数: {}<<<---", FastJsonUtil.objectToString(params));
		PageRecord<Order> orderPageRecord = queryPageByObject(QueryId.QUERY_COUNT_ORDER_ALARM_LIST,
							QueryId.QUERY_ORDER_ALARM_LIST, params, page, limit);
		return ResultUtil.success(orderPageRecord);
	}
	
	private Map<String, Object> buildQueryParamsMap(OrderModel order) {
		Map<String, Object> params = new HashMap<>(10);
		String sysId = order.getSysId();
		if(CommonUtil.isNotBlank(sysId)) {
			params.put("sysId", sysId);
		}
		String sysName = order.getSysName();
		if(CommonUtil.isNotBlank(sysName)) {
			params.put("sysName", sysName);			
		}
		String region = order.getRegion();
		if(CommonUtil.isNotBlank(region)) {
			params.put("region", region);
		}
		String provinceArea = order.getProvinceArea();
		if(CommonUtil.isNotBlank(provinceArea)) {
			params.put("provinceArea", provinceArea);
		}
		String storeCode = order.getStoreCode();
		if(CommonUtil.isNotBlank(storeCode)) {
			params.put("storeCode", storeCode);
		}
		String storeName = order.getStoreName();
		if(CommonUtil.isNotBlank(storeName)) {
			params.put("storeName", storeName);
		}
		String simpleCode = order.getSimpleCode();
		if(CommonUtil.isNotBlank(simpleCode)) {
			params.put("simpleCode", simpleCode);
		}
		String simpleBarCode = order.getSimpleBarCode();
		if(CommonUtil.isNotBlank(simpleBarCode)) {
			params.put("simpleBarCode", simpleBarCode);
		}
		String receiptCode = order.getReceiptCode();
		if(CommonUtil.isNotBlank(receiptCode)) {
			params.put("receiptCode", receiptCode);
		}
		String simpleName = order.getSimpleName();
		if(CommonUtil.isNotBlank(simpleName)) {
			params.put("simpleName", simpleName);
		}
		return params;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void orderAlarmListExcel(CommonDTO common, OrderModel order, HttpServletResponse response) throws Exception {
		Map<String, Object> params = buildQueryParamsMap(order);
		if (common == null || StringUtils.isBlank(common.getStartDate()) || StringUtils.isBlank(common.getEndDate())) {
			throw new DataException("534");
		}
		params.put("startDate", common.getStartDate());
		params.put("endDate", common.getEndDate());
		List<Map<String, Object>> orderReportList = queryListByObject(QueryId.QUERY_ORDER_ALARM_LIST_FOR_REPORT, params);
		for(Map<String, Object> map : orderReportList) {
			String discountAlarmFlag = (String) map.get("discountAlarmFlag");
			map.put("alarmFlag", CommonUtil.isNotBlank(discountAlarmFlag) ? discountAlarmFlag : (String) map.get("contractAlarmFlag"));
		}
		List<String> codeList = codeDictService
				.queryCodeListByServiceCode(CodeEnum.CODE_DICT_ORDER_ALARM_REPORT.value());
		String title = "订单警报报表";
		ExcelUtil excelUtil = new ExcelUtil();
		String fileName = "订单警报报表-" + DateUtil.getCurrentDateStr();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
		excelUtil.exportTemplateByMap(CommonValue.ORDER_ALARM_REPORT_HEADER, orderReportList, title, codeList, response.getOutputStream());
	}
	
	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil uploadOrderData(MultipartFile file) {
		ExcelUtil<Order> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> orderMapList;
		try {
			orderMapList = excelUtil.getExcelList(file, ExcelEnum.ORDER_TEMPLATE_TYPE.value());
			if (orderMapList == null) {
				return ResultUtil.error(CodeEnum.EXCEL_FORMAT_ERROR_DESC.value());
			}
			if (orderMapList.size() == 0) {
				return ResultUtil.error(CodeEnum.DATA_EMPTY_ERROR_DESC.value());
			}
			String orderStr = JSON.toJSONString(orderMapList);
			List<Order> orderList = JSON.parseArray(orderStr, Order.class);
			// 模板门店列表
			List<TemplateStore> storeList = redisService.queryTemplateStoreList();
			// 模板商品列表
			List<TemplateProduct> productList = redisService.queryTemplateProductList();
			// 供应链列表
			List<TemplateSupply> supplyList = queryListByObject(QueryId.QUERY_SUPPLY_BY_CONDITION, new HashMap<>(1));
			dataService.mateOrderData(supplyList, storeList, productList, orderList);
			insert(InsertId.INSERT_ORDER_BATCH_NEW, orderList);
			//insert(InsertId.INSERT_ORDER_BATCH, orderMapList);
		} catch (IOException e) {
			return ResultUtil.error(CodeEnum.UPLOAD_ERROR_DESC.value());
		} catch (Exception se) {
			return ResultUtil.error(CodeEnum.SQL_ERROR_DESC.value());
		}
		return ResultUtil.success();
	}

}
