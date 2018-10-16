package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.Order;
import com.data.bean.PromotionDetail;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.constant.enums.OrderEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.model.OrderModel;
import com.data.service.ICodeDictService;
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
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		list.add("123");
		list.add("132");
		System.err.println(FastJsonUtil.objectToString(list.subList(0, 100)));
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

	@Override
	public ResultUtil getOrderByWeb(String queryDate, String sysId, Integer limit) throws IOException {		
		PageRecord<Order> pageRecord = null;
		logger.info("------>>>>>>开始抓取订单数据<<<<<<---------");		
		logger.info("------>>>>>>系统编号sysId:{},查询时间queryDate:{}<<<<<<<-------", sysId, queryDate);
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		if (CommonUtil.isBlank(sysId)) {
			throw new DataException("503");
		}
		
		// 同步
		synchronized(sysId) {
			logger.info("------>>>>>进入抓取订单同步代码块<<<<<-------");
			Map<String, Object> queryParam = new HashMap<>(2);
			queryParam.put("queryDate", queryDate);
			queryParam.put("sysId", sysId);
			int count = queryCountByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, queryParam);
			
			logger.info("------>>>>>>原数据库中订单数据数量count:{}<<<<<<-------", count);
			List<Order> orderList = null;
			
			if (count == 0) {
				TemplateSupply supply = (TemplateSupply)queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_CONDITION, queryParam);
				
				boolean flag = true;
				
				while (flag) {
					try {
						orderList = dataCaptureUtil.getDataByWeb(queryDate, supply, WebConstant.ORDER, Order.class);
						if (orderList != null) {
							flag = false;
							logger.info("----->>>>抓取订单数据结束<<<<------");
						}
					} catch (Exception e) {
						flag = true;
					}
				}
				
				if (orderList.size() == 0) {
					pageRecord = dataCaptureUtil.setPageRecord(orderList, limit);
					return ResultUtil.success(pageRecord);
				}
				
				List<TemplateStore> storeList = redisService.queryTemplateStoreList();
				List<TemplateProduct> productList = redisService.queryTemplateProductList();
				Order order = null;
				
				// 查询促销明细
				Map<String, Object> param = new HashMap<>(2);
				param.put("sysId", sysId);
				param.put("queryDate", queryDate);
				long start = new Date().getTime();
				logger.info("----->>>>>>>查询促销开始:{}<<<<<<-------", start);
				List<PromotionDetail> promotionList = queryListByObject(QueryId.QUERY_PROMOTION_DETAIL_BY_PARAM, param);
				logger.info("----->>>>>>>查询促销结束:{}<<<<<<--------", new Date().getTime()-start);
				
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
					order.setSysId(sysId);
					// 系统名称
					String sysName = supply.getSysName();
					
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
					
					order.setSysName(supply.getRegion() + sysName);
					
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
					
					// 含税合同供价
					contractPrice = product.getIncludeTaxPrice();
					order.setContractPrice(contractPrice);
					
					// 含税进价
					buyPriceWithTax = order.getBuyingPriceWithRate();
					
					int j = 0;
					int promotionSize = 0;
					for (j = 0, promotionSize = promotionList.size(); j < promotionSize; j++) {
						promotionDetail = promotionList.get(j);
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
				// 插入数据
				logger.info("----->>>>>>开始插入订单数据<<<<<<------");
				dataCaptureUtil.insertData(orderList, InsertId.INSERT_BATCH_ORDER);
			} else {
				orderList = queryListByObject(QueryId.QUERY_ORDER_BY_CONDITION, queryParam);
			}
			
			pageRecord = dataCaptureUtil.setPageRecord(orderList, limit);
		}
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
	public ResultUtil queryOrderAlarmList(OrderModel order, Integer page, Integer limit) throws Exception {
		Map<String, Object> params = buildQueryParamsMap(order);
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
	public void orderAlarmListExcel(OrderModel order, HttpServletResponse response) throws Exception {
		Map<String, Object> params = buildQueryParamsMap(order);
		List<Map<String, Object>> orderReportList = queryListByObject(QueryId.QUERY_ORDER_ALARM_LIST_FOR_REPORT, params);
		for(Map<String, Object> map : orderReportList) {
			String discountAlarmFlag = (String) map.get("discountAlarmFlag");
			map.put("alarmFlag", CommonUtil.isNotBlank(discountAlarmFlag) ? discountAlarmFlag : (String) map.get("contractAlarmFlag"));
		}
		List<String> codeList = codeDictService
				.queryCodeListByServiceCode(CodeEnum.CODE_DICT_ORDER_ALARM_REPORT.getValue());
		String title = "订单警报报表";
		ExcelUtil excelUtil = new ExcelUtil();
		String fileName = "订单警报报表-" + DateUtil.getCurrentDateStr();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
		excelUtil.exportTemplateByMap(CommonValue.ORDER_ALARM_REPORT_HEADER, orderReportList, title, codeList, response.getOutputStream());
	}

}
