package com.data.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONPath;
import com.data.bean.DataLog;
import com.data.bean.Order;
import com.data.bean.PromotionDetail;
import com.data.bean.PromotionStoreList;
import com.data.bean.Reject;
import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.TipsEnum;
import com.data.exception.GlobalException;
import com.data.service.IDataService;
import com.data.service.IOrderService;
import com.data.service.IRedisService;
import com.data.service.IRejectService;
import com.data.service.ISaleService;
import com.data.service.IStockService;
import com.data.utils.CommonUtil;
import com.data.utils.DateUtil;
import com.data.utils.ResultUtil;
import com.data.utils.TemplateDataUtil;

@Service
public class DataServiceImpl extends CommonServiceImpl implements IDataService{
	private static Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);
	@Autowired
	private IRedisService redisService;
	@Autowired
	private TemplateDataUtil templateDataUtil;
	
	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil completionData() throws Exception {
		Map<String, Object> mapObj = new HashMap<>(1);
		mapObj.put("status", 0);
		List<Order> orderList = queryListByObject(QueryId.QUERY_ORDER_BY_CONDITION, mapObj);
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, mapObj);
		List<Reject> rejectList = queryListByObject(QueryId.QUERY_REJECT_BY_PARAM, mapObj);
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_PARAM, mapObj);
		
		// 删除未匹配数据
		delete(DeleteId.DELETE_ORDER_BY_PARAM, mapObj);
		delete(DeleteId.DELETE_SALE_BY_PARAM, mapObj);
		delete(DeleteId.DELETE_REJECT_BY_PARAM, mapObj);
		delete(DeleteId.DELETE_STOCK_BY_PARAM, mapObj);
		
		// 模板门店列表
		List<TemplateStore> storeList = redisService.queryTemplateStoreList();
		// 模板商品列表
		List<TemplateProduct> productList = redisService.queryTemplateProductList();
		// 供应链列表
		List<TemplateSupply> supplyList = queryListByObject(QueryId.QUERY_SUPPLY_BY_CONDITION, mapObj);
		
		if (!CollectionUtils.isEmpty(orderList)) {
			logger.info("匹配订单数据");
			mateOrderData(supplyList, storeList, productList, orderList);
		}
		if (!CollectionUtils.isEmpty(saleList)) {
			logger.info("匹配销售数据");
			mateSaleData(supplyList, storeList, productList, saleList);
		}
		if (!CollectionUtils.isEmpty(stockList)) {
			logger.info("匹配库存数据");
			mateStockData(supplyList, storeList, productList, stockList);
		}
		if (!CollectionUtils.isEmpty(rejectList)) {
			logger.info("匹配退单数据");
			mateRejectData(supplyList, storeList, productList, rejectList);
		}
		logger.info("插入订单数据");
		insert(InsertId.INSERT_ORDER_BATCH_NEW, orderList);
		logger.info("插入退单数据");
		insert(InsertId.INSERT_REJECT_BATCH, rejectList);
		logger.info("插入销售数据");
		insert(InsertId.INSERT_SALE_BATCH, saleList);
		logger.info("插入库存数据");
		insert(InsertId.INSERT_STOCK_BATCH, stockList);
		return ResultUtil.success();
	}
	
	public void mateOrderData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Order> orderList) throws Exception {
		for (int i = 0; i < orderList.size(); i++) {
			Order order = orderList.get(i);
			String sysId = order.getSysId();
			TemplateSupply supply = ((List<TemplateSupply>)JSONPath.eval(supplyList, "$[sysId='"+ sysId +"']")).get(0);
			String sysName = supply.getSysName();
			String region = supply.getRegion();
			String simpleCode = order.getSimpleCode();
			String simpleBarCode = order.getSimpleBarCode();
			Date queryDate = order.getDeliverStartDate();
			String storeCode = order.getStoreCode();
		
			order.setStatus(1);
			order.setSysName(region+sysName);
			// 条码信息
			if (StringUtils.isBlank(simpleBarCode)) {
				try {
					simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
					if (CommonUtil.isBlank(simpleBarCode)) {
	
						// 错误日志
						DataLog log = new DataLog();
						log.setRegion(region);
						log.setLogDate(queryDate);
						log.setSysId(sysId);
						log.setSysName(sysName);
						log.setLogRemark("编码" + simpleCode + "商品" + TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						insert(InsertId.INSERT_DATA_LOG, log);
						order.setStatus(0);
						// order.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						continue;
					}
				} catch (GlobalException e) {
					// 错误日志
					DataLog log = new DataLog();
					log.setRegion(region);
					log.setLogDate(queryDate);
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
	
			// 标准单品信息
			TemplateProduct product = null;

			/*String tempSysId = null;
			String tempSimpleBarCode = null;
			for (int j = 0, len = productList.size(); j < len; j++) {
				product = productList.get(j);
				tempSysId = product.getSysId();
				tempSimpleBarCode = product.getSimpleBarCode();
				if (sysId.equals(tempSysId) && simpleBarCode.equals(tempSimpleBarCode)) {
					break;
				}
				product = null;
			}*/
			product = mateTemplateProduct(sysId, simpleBarCode, productList);

			TemplateStore store = null;
			/*String tempStoreCode = null;
			for (int j = 0, len = storeList.size(); j < len; j++) {
				store = storeList.get(j);
				tempSysId = store.getSysId();
				tempStoreCode = store.getStoreCode();
				if (sysId.equals(tempSysId) && storeCode.equals(tempStoreCode)) {
					break;
				}
				store = null;
			}*/
			store = mateTemplateStore(sysId, storeCode, storeList);
			if (CommonUtil.isBlank(product)) {
	
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(queryDate);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				order.setStatus(0);
				// order.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				// continue;
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
				BigDecimal contractPrice = product.getIncludeTaxPrice();
				order.setContractPrice(contractPrice);
	
				// 含税进价
				BigDecimal buyPriceWithTax = order.getBuyingPriceWithRate();
				
				// 查询促销明细
				Map<String, Object> param = new HashMap<>(2);
				param.put("sysId", sysId);
				param.put("queryDate", DateUtil.format(queryDate));
				List<PromotionDetail> promotionList = queryListByObject(QueryId.QUERY_PROMOTION_DETAIL_BY_PARAM, param);
	
				int j = 0;
				int promotionSize = 0;
				for (j = 0, promotionSize = promotionList.size(); j < promotionSize; j++) {
					PromotionDetail promotionDetail = promotionList.get(j);
					Integer detailId = promotionDetail.getId();
					List<PromotionStoreList> promotionStoreList = queryListByObject(
							QueryId.QUERY_PROMOTION_STORE_LIST_BY_DETAIL_ID, detailId);
	
					if (JSONPath.eval(promotionStoreList, "$[storeCode='" + storeCode + "']") != null) {
						if (simpleBarCode.equals(promotionDetail.getProductCode())) {
							order.setOrderEffectiveJudge("是促销期内订单");
							// 促销供价开始、结束时间
							order.setDiscountStartDate(promotionDetail.getSupplyPriceStartDate());
							order.setDiscountEndDate(promotionDetail.getSupplyPriceEndDate());
	
							// 补差方式
							order.setBalanceWay(promotionDetail.getCompensationType());
	
							// 供价方式
							String supplyOrderType = promotionDetail.getSupplyOrderType();
	
							if ("特供价入库".equals(supplyOrderType)) {
	
								// 促销供价
								BigDecimal supplyPrice = promotionDetail.getSupplyPrice();
								order.setDiscountPrice(supplyPrice);
	
								// 促销供价差异
								order.setDiffPriceDiscount(buyPriceWithTax.subtract(supplyPrice));
	
								// 促销供价差异汇总
								order.setDiffPriceDiscountTotal(
										order.getDiffPriceDiscount().multiply(new BigDecimal(order.getSimpleAmount())));
	
								// 供价示警
								if (buyPriceWithTax.compareTo(supplyPrice) < 0) {
									order.setDiscountAlarmFlag("订单低于促销供价");
								}
	
							} else if ("原价入库".equals(supplyOrderType)) {
	
								// 合同供价差异
								order.setDiffPriceContract(buyPriceWithTax.subtract(contractPrice));
	
								// 合同供价差异汇总
								order.setDiffPriceContractTotal(
										order.getDiffPriceContract().multiply(new BigDecimal(order.getSimpleAmount())));
	
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
					order.setDiffPriceContractTotal(
							order.getDiffPriceContract().multiply(new BigDecimal(order.getSimpleAmount())));
	
					if (buyPriceWithTax.compareTo(contractPrice) < 0) {
						order.setContractAlarmFlag("订单低于合同供价");
					}
				}
	
				// 汇总差异
				order.setDiffPrice(order.getDiffPriceContractTotal().add(
						order.getDiffPriceDiscountTotal() == null ? new BigDecimal(0) : order.getDiffPriceDiscountTotal()));
			}
	
			if (null == store) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(queryDate);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				order.setStatus(0);
				// order.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				// continue;
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
	public void mateSaleData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Sale> saleList) throws Exception {
		for (int i = 0; i < saleList.size(); i++) {
			Sale sale = saleList.get(i);
			String sysId = sale.getSysId();
			TemplateSupply supply = ((List<TemplateSupply>)JSONPath.eval(supplyList, "$[sysId='"+ sysId +"']")).get(0);
			String sysName = supply.getSysName();
			String region = supply.getRegion();
			String simpleCode = sale.getSimpleCode();
			String simpleBarCode = sale.getSimpleBarCode();
			Date queryDate = sale.getCreateTime();
			String storeCode = sale.getStoreCode();
			
			sale.setStatus(1);
			sale.setSysName(region+sysName);
			if (StringUtils.isBlank(simpleBarCode)) {
				try {
					simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
					if (CommonUtil.isBlank(simpleBarCode)) {

						// 错误日志
						DataLog log = new DataLog();
						log.setRegion(region);
						log.setLogDate(queryDate);
						log.setSysId(sysId);
						log.setSysName(sysName);
						log.setLogRemark("编码" + simpleCode + "商品" + TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						insert(InsertId.INSERT_DATA_LOG, log);
						sale.setStatus(0);
						// order.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						continue;
					}
				} catch (GlobalException e) {
					// 错误日志
					DataLog log = new DataLog();
					log.setRegion(region);
					log.setLogDate(queryDate);
					log.setSysId(sysId);
					log.setSysName(sysName);
					log.setLogRemark(e.getErrorMsg());
					insert(InsertId.INSERT_DATA_LOG, log);
					sale.setStatus(0);
					continue;
				}
			}

			sale.setSimpleBarCode(simpleBarCode);

			// 标准单品信息
			TemplateProduct product = null;

			/*String tempSysId = null;
			String tempSimpleBarCode = null;
			for (int j = 0, len = productList.size(); j < len; j++) {
				product = productList.get(j);
				tempSysId = product.getSysId();
				tempSimpleBarCode = product.getSimpleBarCode();
				if (sysId.equals(tempSysId) && simpleBarCode.equals(tempSimpleBarCode)) {
					break;
				}
				product = null;
			}*/
			product = mateTemplateProduct(sysId, simpleBarCode, productList);

			TemplateStore store = null;
			/*String tempStoreCode = null;
			for (int j = 0, len = storeList.size(); j < len; j++) {
				store = storeList.get(j);
				tempSysId = store.getSysId();
				tempStoreCode = store.getStoreCode();
				if (sysId.equals(tempSysId) && storeCode.equals(tempStoreCode)) {
					break;
				}
				store = null;
			}*/
			store = mateTemplateStore(sysId, storeCode, storeList);

			// 单品信息为空
			if (CommonUtil.isBlank(product)) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(queryDate);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				sale.setStatus(0);
				// sale.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				// continue;
			} else {
				// 单品名称
				sale.setSimpleName(product.getStandardName());

				// 品牌
				sale.setBrand(product.getBrand());

				// 销售价格
				// sale.setSellPrice(product.getSellPrice());

				// 系列
				sale.setSeries(product.getSeries());

				// 材质
				sale.setMaterial(product.getMaterial());

				// 片数
				sale.setPiecesNum(product.getPiecesNum());

				// 日夜
				sale.setDayNight(product.getFunc());

				// 货号
				sale.setStockNo(product.getStockNo());

				// 箱规
				sale.setBoxStandard(product.getBoxStandard());

				// 库存编号
				sale.setStockCode(product.getStockCode());
			}

			// 门店信息为空
			if (CommonUtil.isBlank(store)) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(queryDate);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				sale.setStatus(0);
				// sale.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				// continue;
			} else {
				// 大区
				sale.setRegion(store.getRegion());

				// 省区
				sale.setProvinceArea(store.getProvinceArea());

				// 门店名称
				sale.setStoreName(store.getStandardStoreName());

				// 归属
				sale.setAscription(store.getAscription());

				// 业绩归属
				sale.setAscriptionSole(store.getAscriptionSole());

				// 门店负责人
				sale.setStoreManager(store.getStoreManager());
			}
		}
	}
	public void mateRejectData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Reject> rejectList) throws Exception {
		for (int i = 0, size = rejectList.size(); i < size; i++) {
			Reject reject = rejectList.get(i);
			
			String sysId = reject.getSysId();
			
			TemplateSupply supply = ((List<TemplateSupply>)JSONPath.eval(supplyList, "$[sysId='"+ sysId +"']")).get(0);
			String sysName = supply.getSysName();
			String region = supply.getRegion();
			
			// 单品编码
			String simpleCode = reject.getSimpleCode();
			
			// 单品条码
			String simpleBarCode = reject.getSimpleBarCode();
			
			// 门店编码
			String storeCode = reject.getRejectDepartmentId();
			
			Date queryDate = reject.getRejectDate();
			
			reject.setStatus(1);
			reject.setSysName(region+sysName);
			// 条码信息
			if (StringUtils.isBlank(simpleBarCode)) {
				try {
					simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
					if (CommonUtil.isBlank(simpleBarCode)) {
						// 错误日志
						DataLog log = new DataLog();
						log.setRegion(region);
						log.setLogDate(queryDate);
						log.setSysId(sysId);
						log.setSysName(sysName);
						log.setLogRemark("编码"+simpleCode+"商品"+TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						insert(InsertId.INSERT_DATA_LOG, log);
						reject.setStatus(0);
						//reject.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						continue;
					}
				} catch (GlobalException e) {
					// 错误日志
					DataLog log = new DataLog();
					log.setRegion(region);
					log.setLogDate(queryDate);
					log.setSysId(sysId);
					log.setSysName(sysName);
					log.setLogRemark(e.getErrorMsg());
					insert(InsertId.INSERT_DATA_LOG, log);
					reject.setStatus(0);
					continue;
				}
				
			}
			
			// 单品条码
			reject.setSimpleBarCode(simpleBarCode);
			
			// 标准单品信息
			TemplateProduct product = null;

			/*String tempSysId = null;
			String tempSimpleBarCode = null;
			for (int j = 0, len = productList.size(); j < len; j++) {
				product = productList.get(j);
				tempSysId = product.getSysId();
				tempSimpleBarCode = product.getSimpleBarCode();
				if (sysId.equals(tempSysId) && simpleBarCode.equals(tempSimpleBarCode)) {
					break;
				}
				product = null;
			}*/
			product = mateTemplateProduct(sysId, simpleBarCode, productList);

			TemplateStore store = null;
			/*String tempStoreCode = null;
			for (int j = 0, len = storeList.size(); j < len; j++) {
				store = storeList.get(j);
				tempSysId = store.getSysId();
				tempStoreCode = store.getStoreCode();
				if (sysId.equals(tempSysId) && storeCode.equals(tempStoreCode)) {
					break;
				}
				store = null;
			}*/
			store = mateTemplateStore(sysId, storeCode, storeList);
			if (CommonUtil.isBlank(product)) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(queryDate);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码"+simpleBarCode+"商品"+TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				reject.setStatus(0);
				//reject.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				//continue;
			} else {
			
				// 单品名称
				reject.setSimpleName(product.getStandardName());
					
				// 库存编号
				reject.setStockCode(product.getStockCode());
				
				// 含税合同供价
				BigDecimal contractPrice = product.getIncludeTaxPrice();
				reject.setContractPrice(contractPrice);
				
				// 退货价格
				BigDecimal rejectPrice = reject.getRejectPrice();
				
				// 查询促销明细
				Map<String, Object> param = new HashMap<>(2);
				param.put("sysId", sysId);
				param.put("queryDate", DateUtil.format(queryDate));
				List<PromotionDetail> promotionList = queryListByObject(QueryId.QUERY_PROMOTION_DETAIL_BY_PARAM, param);
				
				int j = 0;
				int len = 0;
				for (j = 0, len = promotionList.size(); j < len; j++) {
					PromotionDetail promotionDetail = promotionList.get(j);
					if (simpleBarCode.equals(promotionDetail.getProductCode())) {
						// 促销供价开始、结束时间
						reject.setDiscountStartDate(promotionDetail.getSupplyPriceStartDate());
						reject.setDiscountEndDate(promotionDetail.getSupplyPriceEndDate());
						
						// 供价方式
						String supplyOrderType = promotionDetail.getSupplyOrderType();
						
						if ("特供价入库".equals(supplyOrderType)) {				
							
							// 促销供价
							BigDecimal supplyPrice = promotionDetail.getSupplyPrice();
							reject.setDiscountPrice(supplyPrice);
							
							// 促销供价差异
							reject.setDiffPriceDiscount(rejectPrice.subtract(supplyPrice));
							
							// 促销供价差异汇总
							reject.setDiffPriceDiscountTotal(reject.getDiffPriceDiscount().multiply(new BigDecimal(reject.getSimpleAmount())));
							
							// 供价示警
							if (rejectPrice.compareTo(supplyPrice) > 0) {
								reject.setDiscountAlarmFlag("退货价格高于促销供价，请检查促销是否已经生效");
							}
							
						} else if ("原价入库".equals(supplyOrderType)) {
							
							// 合同供价差异
							reject.setDiffPriceContract(rejectPrice.subtract(contractPrice));
							
							// 合同供价差异汇总
							reject.setDiffPriceContractTotal(reject.getDiffPriceContract().multiply(new BigDecimal(reject.getSimpleAmount())));
							
							if (rejectPrice.compareTo(contractPrice) > 0) {
								reject.setDiscountAlarmFlag("退货价格高于合同供价，处于促销日期");
							}
						}
						break;
						
					}
				}
				
				// 不处于促销范围之内
				if (j == len) {
					
					// 含税合同供价
					contractPrice = product.getIncludeTaxPrice();
					reject.setContractPrice(contractPrice);
					
					// 合同供价差异
					reject.setDiffPriceContract(rejectPrice.subtract(contractPrice));
					
					// 合同供价差异汇总
					reject.setDiffPriceContractTotal(reject.getDiffPriceContract().multiply(new BigDecimal(reject.getSimpleAmount())));
					
					if (rejectPrice.compareTo(contractPrice) > 0) {
						reject.setContractAlarmFlag("没有促销信息，退单价高于合同价");
					}
				}
				
				// 汇总差异
				reject.setDiffPrice(reject.getDiffPriceContractTotal().add(reject.getDiffPriceDiscountTotal()==null?new BigDecimal(0):reject.getDiffPriceDiscountTotal()));
			}
			
			// 门店信息为空
			if (CommonUtil.isBlank(store)) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(queryDate);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码"+simpleBarCode+"商品"+TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				reject.setStatus(0);
				//reject.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				//continue;
			} else {
				// 大区
				reject.setRegion(store.getRegion());
					
				// 省区
				reject.setProvinceArea(store.getProvinceArea());
					
				// 门店名称
				reject.setRejectDepartmentName(store.getStandardStoreName());
			}
		}
	}
	public void mateStockData(List<TemplateSupply> supplyList, List<TemplateStore> storeList, List<TemplateProduct> productList, List<Stock> stockList) throws Exception {
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			
			// 单品编号
			String simpleCode = stock.getSimpleCode();
			
			// 系统ID
			String sysId = stock.getSysId();

			// 系统名称
			TemplateSupply supply = ((List<TemplateSupply>)JSONPath.eval(supplyList, "$[sysId='"+ sysId +"']")).get(0);
			String sysName = supply.getSysName();
			String region = supply.getRegion();

			// 门店编号
			String storeCode = stock.getStoreCode();

			// 商品条码
			String simpleBarCode = stock.getSimpleBarCode();
			
			Date now = stock.getCreateTime();
			stock.setStatus(1);
			stock.setSysName(region+sysName);
			// 标准条码匹配信息
			if (StringUtils.isBlank(simpleBarCode)) {				
				try {
					simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
					if (CommonUtil.isBlank(simpleBarCode)) {

						// 错误日志
						DataLog log = new DataLog();
						log.setRegion(region);
						log.setLogDate(now);
						log.setSysId(sysId);
						log.setSysName(sysName);
						log.setLogRemark("编码" + simpleCode + "商品" + TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						insert(InsertId.INSERT_DATA_LOG, log);
						stock.setStatus(0);
						// order.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						continue;
					}
				} catch (GlobalException e) {
					// 错误日志
					DataLog log = new DataLog();
					log.setRegion(region);
					log.setLogDate(now);
					log.setSysId(sysId);
					log.setSysName(sysName);
					log.setLogRemark(e.getErrorMsg());
					insert(InsertId.INSERT_DATA_LOG, log);
					stock.setStatus(0);
					continue;
				}
			}
			stock.setSimpleBarCode(simpleBarCode);

			// 标准单品信息
			TemplateProduct product = null;

			/*String tempSysId = null;
			String tempSimpleBarCode = null;
			for (int j = 0, len = productList.size(); j < len; j++) {
				product = productList.get(j);
				tempSysId = product.getSysId();
				tempSimpleBarCode = product.getSimpleBarCode();
				if (sysId.equals(tempSysId) && simpleBarCode.equals(tempSimpleBarCode)) {
					break;
				}
				product = null;
			}*/
			product = mateTemplateProduct(sysId, simpleBarCode, productList);

			TemplateStore store = null;
			/*String tempStoreCode = null;
			for (int j = 0, len = storeList.size(); j < len; j++) {
				store = storeList.get(j);
				tempSysId = store.getSysId();
				tempStoreCode = store.getStoreCode();
				if (sysId.equals(tempSysId) && storeCode.equals(tempStoreCode)) {
					break;
				}
				store = null;
			}*/			
			store = mateTemplateStore(sysId, storeCode, storeList);

			// 门店信息为空
			if (CommonUtil.isBlank(store)) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(now);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				stock.setStatus(0);
				//stock.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
			} else {
				// 大区
				stock.setRegion(store.getRegion());

				// 省区
				stock.setProvinceArea(store.getProvinceArea());

				// 门店名称
				stock.setStoreName(store.getStandardStoreName());

				// 归属
				stock.setAscription(store.getAscription());

				// 业绩归属
				stock.setAscriptionSole(store.getAscriptionSole());
			}

			// 单品信息为空
			if (CommonUtil.isBlank(product)) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(now);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				stock.setStatus(0);
				//stock.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				//continue;
			} else {
				// 单品名称
				stock.setSimpleName(product.getStandardName());

				// 品牌
				stock.setBrand(product.getBrand());

				// 系列
				stock.setSeries(product.getSeries());

				// 材质
				stock.setMaterial(product.getMaterial());

				// 片数
				stock.setPiecesNum(product.getPiecesNum());

				// 日夜
				stock.setDayNight(product.getFunc());

				// 货号
				stock.setStockNo(product.getStockNo());

				// 箱规
				stock.setBoxStandard(product.getBoxStandard());

				// 库存编号
				stock.setStockCode(product.getStockCode());

				// 类别
				stock.setClassify(product.getClassify());
			}
		}
	}
	
	/**
	 * 匹配模板门店
	 * @param sysId
	 * @param storeCode
	 * @param storeList
	 * @return
	 */
	private TemplateStore mateTemplateStore(String sysId, String storeCode, List<TemplateStore> storeList) {
		List<TemplateStore> templateStoreList = (List<TemplateStore>) JSONPath.eval(storeList, "$[sysId='"+ sysId +"']");
		templateStoreList = (List<TemplateStore>) JSONPath.eval(storeList, "$[storeCode='"+ storeCode +"']");
		if (!CollectionUtils.isEmpty(templateStoreList)) {
			TemplateStore store = templateStoreList.get(0);
			return store;
		}
		return null;
	}
	/**
	 * 匹配模板单品
	 * @param sysId
	 * @param simpleBarCode
	 * @param productList
	 * @return
	 */
	private TemplateProduct mateTemplateProduct(String sysId, String simpleBarCode, List<TemplateProduct> productList) {
		List<TemplateProduct> templateProductList = (List<TemplateProduct>) JSONPath.eval(productList, "$[sysId='"+ sysId +"']");
		templateProductList = (List<TemplateProduct>) JSONPath.eval(productList, "$[simpleBarCode='"+ simpleBarCode +"']");
		if (!CollectionUtils.isEmpty(templateProductList)) {
			TemplateProduct product = templateProductList.get(0);
			return product;
		}
		return null;
	}
}
