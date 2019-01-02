package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.PromotionDetail;
import com.data.bean.Reject;
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
import com.data.constant.enums.RejectEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.model.RejectModel;
import com.data.service.ICodeDictService;
import com.data.service.IRedisService;
import com.data.service.IRejectService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.DateUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.ExportUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.data.utils.TemplateDataUtil;

@Service
public class RejectServiceImpl extends CommonServiceImpl implements IRejectService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IRedisService redisService;
	
	@Autowired
	private DataCaptureUtil dataCaptureUtil;
	
	@Autowired
	private TemplateDataUtil templateDataUtil;
	
	@Autowired
	private ExportUtil exportUtil;
	
	@Autowired
	private ICodeDictService codeDictService;

	@Override
	public ResultUtil getRejectByWeb(String queryDate, Integer id, Integer limit) throws Exception {
		PageRecord<Reject> pageRecord = null;
		logger.info("------>>>>>>开始抓取退单数据<<<<<<---------");
		logger.info("------>>>>>>系统id:{},查询时间queryDate:{}<<<<<<<-------", id, queryDate);
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		if (id == null || id == 0) {
			throw new Exception("id不能为空");
		}
		
		// 同步
		synchronized (id) {
			logger.info("------->>>>>>>进入抓取退单同步代码块<<<<<<<-------");
			Map<String, Object> queryParam = new HashMap<>(2);
			queryParam.put("id", id);
			
			// 供应链数据
			TemplateSupply supply = (TemplateSupply)queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_CONDITION, queryParam);
			if (supply == null) {
				return ResultUtil.error("供应链未找到");
			}
			String sysId = supply.getSysId();
			
			// 查询退单数据是否已存在
			queryParam.clear();
			queryParam.put("sysId", sysId);
			queryParam.put("queryDate", queryDate);
			int count = queryCountByObject(QueryId.QUERY_COUNT_REJECT_BY_PARAM, queryParam);
			
			logger.info("------>>>>>>原数据库中退单数据数量count:{}<<<<<<-------", count);
			List<Reject> rejectList = null;
			
			String rejectStr = null;
			
			if (count == 0) {
				
				
				//boolean flag = true;
				
				/*while (flag) {
					try {*/
						rejectStr = dataCaptureUtil.getDataByWeb(queryDate, supply, WebConstant.REJECT);
						/*if (rejectStr != null) {
							flag = false;
							logger.info("----->>>>抓取退单数据结束<<<<------");
						}
					} catch (DataException e) {
						return ResultUtil.error(e.getMessage());
					} catch (Exception e) {
						flag = true;
					}
				}*/
				
				// 判断是否为解析excel表
				rejectList = (List<Reject>) FastJsonUtil.jsonToList(rejectStr, Reject.class);

				if (CollectionUtils.isEmpty(rejectList)) {
					pageRecord = dataCaptureUtil.setPageRecord(rejectList, limit);
					return ResultUtil.success(pageRecord);
				}
				
				
				
				List<TemplateStore> storeList = redisService.queryTemplateStoreList();
				List<TemplateProduct> productList = redisService.queryTemplateProductList();
				Reject  reject = null;
				
				// 查询促销明细
				Map<String, Object> param = new HashMap<>(2);
				param.put("sysId", sysId);
				param.put("queryDate", queryDate);
				long start = new Date().getTime();
				logger.info("----->>>>>>>查询促销:{}<<<<<<-------", start);
				List<PromotionDetail> promotionList = queryListByObject(QueryId.QUERY_PROMOTION_DETAIL_BY_PARAM, param);
				logger.info("----->>>>>>>查询结束:{}<<<<<<--------", new Date().getTime()-start);
				
				PromotionDetail promotionDetail = null;
				
				// 入库方式
				String supplyOrderType = null;
				
				// 含税进价
				BigDecimal rejectPrice = null;
				
				// 促销供价
				BigDecimal supplyPrice = null;
				
				// 含税合同供价
				BigDecimal contractPrice = null;
				for (int i = 0, size = rejectList.size(); i < size; i++) {
					reject = rejectList.get(i);
					reject.setSysId(sysId);
					// 系统名称
					String sysName = supply.getSysName();
					
					// 单品编码
					String simpleCode = reject.getSimpleCode();
					
					// 单品条码
					String simpleBarCode = reject.getSimpleBarCode();
					
					// 门店编码
					String storeCode = reject.getRejectDepartmentId();
					
					// 条码信息
					if (StringUtils.isBlank(simpleBarCode)) {
						simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
						if (CommonUtil.isBlank(simpleBarCode)) {
							reject.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
							continue;
						}
					}
	
					reject.setSysName(supply.getRegion() + sysName);
					
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
						reject.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
						continue;
					}
					
					// 单品条码
					reject.setSimpleBarCode(simpleBarCode);
					
					// 单品名称
					reject.setSimpleName(product.getStandardName());
						
					// 库存编号
					reject.setStockCode(product.getStockCode());
					
					// 含税合同供价
					contractPrice = product.getIncludeTaxPrice();
					reject.setContractPrice(contractPrice);
					
					// 退货价格
					rejectPrice = reject.getRejectPrice();
					
					int j = 0;
					int len = 0;
					for (j = 0, len = promotionList.size(); j < len; j++) {
						promotionDetail = promotionList.get(j);
						if (simpleBarCode.equals(promotionDetail.getProductCode())) {
							// 促销供价开始、结束时间
							reject.setDiscountStartDate(promotionDetail.getSupplyPriceStartDate());
							reject.setDiscountEndDate(promotionDetail.getSupplyPriceEndDate());
							
							// 供价方式
							supplyOrderType = promotionDetail.getSupplyOrderType();
							
							if ("特供价入库".equals(supplyOrderType)) {				
								
								// 促销供价
								supplyPrice = promotionDetail.getSupplyPrice();
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
					
					// 单品门店信息
					TemplateStore store = null;
					String tempStoreCode = null;
					String tempOrderStoreName = null;
					for (j = 0, len = storeList.size(); j < len; j++) {
						store = storeList.get(j);
						tempSysId = store.getSysId();
						tempStoreCode = store.getStoreCode();
						tempOrderStoreName = store.getOrderStoreName() == null ? "" : store.getOrderStoreName();
						if (sysId.equals(tempSysId) && (storeCode.equals(tempStoreCode) || tempOrderStoreName.contains(storeCode))) {
							break;
						}
						store = null;
					}
					
					// 门店信息为空
					if (CommonUtil.isBlank(store)) {
						reject.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
						continue;
					} 
					// 大区
					reject.setRegion(store.getRegion());
						
					// 省区
					reject.setProvinceArea(store.getProvinceArea());
						
					// 门店名称
					reject.setRejectDepartmentName(store.getStandardStoreName());
				}
				// 插入数据
				logger.info("------>>>>>开始插入退单数据<<<<<-------");
				insert(InsertId.INSERT_REJECT_BATCH, rejectList);
			} else {
				rejectList = queryListByObject(QueryId.QUERY_REJECT_BY_PARAM, queryParam);
			}
			
			pageRecord = dataCaptureUtil.setPageRecord(rejectList, limit);
			
		}
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getRejectByParam(CommonDTO common, Reject reject, Integer page, Integer limit) throws Exception {
		logger.info("--->>>退单查询参数common: {}<<<---", FastJsonUtil.objectToString(common));
		logger.info("---->>>reject:{}<<<------", FastJsonUtil.objectToString(reject));
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
		if (null != reject) {
			if (CommonUtil.isNotBlank(reject.getSysId())) {
				map.put("sysId", reject.getSysId());
			}
			if (CommonUtil.isNotBlank(reject.getSimpleBarCode())) {
				map.put("simpleBarCode", reject.getSimpleBarCode());
			}
			if (CommonUtil.isNotBlank(reject.getReceiptCode())) {
				map.put("receiptCode", reject.getReceiptCode());
			}
			if (CommonUtil.isNotBlank(reject.getRejectDepartmentId())) {
				map.put("rejectDepartmentId", reject.getRejectDepartmentId());
			}
		}
		PageRecord<Reject> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_REJECT_BY_PARAM, QueryId.QUERY_REJECT_BY_PARAM, map, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public void exportRejectExcel(String stockNameStr, CommonDTO common, Reject reject, OutputStream output)
			throws Exception {
		logger.info("----->>>>自定义字段：{}<<<<------", stockNameStr);
		logger.info("----->>>>common：{}<<<<------", FastJsonUtil.objectToString(common));
		logger.info("----->>>>reject：{}<<<<------", FastJsonUtil.objectToString(reject));
		exportUtil.exportConditionJudge(common, reject, stockNameStr);
		String[] header = CommonUtil.parseIdsCollection(stockNameStr, ",");
		Map<String, Object> map = exportUtil.joinColumn(RejectEnum.class, header);
		
		// 调用方法名
		String[] methodNameArray = (String[]) map.get("methodNameArray");
		
		// 导出字段
		String column = (String) map.get("column");
		
		// 自选导出excel表查询字段
		Map<String, Object> param = exportUtil.joinParam(common.getStartDate(), common.getEndDate(), column,
				reject.getSysId());

		if (CommonUtil.isNotBlank(reject.getSysId())) {
			param.put("sysId", reject.getSysId());
		}
		if (CommonUtil.isNotBlank(reject.getSimpleBarCode())) {
			param.put("simpleBarCode", reject.getSimpleBarCode());
		}
		if (CommonUtil.isNotBlank(reject.getReceiptCode())) {
			param.put("receiptCode", reject.getReceiptCode());
		}
		if (CommonUtil.isNotBlank(reject.getRejectDepartmentId())) {
			param.put("rejectDepartmentId", reject.getRejectDepartmentId());
		}
		logger.info("------->>>>>导出条件：{}<<<<<-------", FastJsonUtil.objectToString(param));
		List<Reject> dataList = queryListByObject(QueryId.QUERY_REJECT_BY_ANY_COLUMN, param);

		ExcelUtil<Reject> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007("退单信息", header, methodNameArray, dataList, output);
	}

	@Override
	public ResultUtil queryRejectAlarmList(CommonDTO common, RejectModel reject, Integer page, Integer limit) throws Exception {
		Map<String, Object> params = buildQueryParamsMap(reject);
		if (common == null || StringUtils.isBlank(common.getStartDate()) || StringUtils.isBlank(common.getEndDate())) {
			throw new DataException("534");
		}
		params.put("startDate", common.getStartDate());
		params.put("endDate", common.getEndDate());
		logger.info("--->>>订单报警列表查询参数: {}<<<---", FastJsonUtil.objectToString(params));
		PageRecord<Reject> rejectPageRecord = queryPageByObject(QueryId.QUERY_COUNT_REJECT_ALARM_LIST,
							QueryId.QUERY_REJECT_ALARM_LIST, params, page, limit);
		return ResultUtil.success(rejectPageRecord);
	}
	
	private Map<String, Object> buildQueryParamsMap(RejectModel reject) {
		Map<String, Object> params = new HashMap<>(10);
		String sysId = reject.getSysId();
		if(CommonUtil.isNotBlank(sysId)) {
			params.put("sysId", sysId);
		}
		String sysName = reject.getSysName();
		if(CommonUtil.isNotBlank(sysName)) {
			params.put("sysName", sysName);			
		}
		String region = reject.getRegion();
		if(CommonUtil.isNotBlank(region)) {
			params.put("region", region);
		}
		String provinceArea = reject.getProvinceArea();
		if(CommonUtil.isNotBlank(provinceArea)) {
			params.put("provinceArea", provinceArea);
		}
		String rejectDepartmentId = reject.getRejectDepartmentId();
		if(CommonUtil.isNotBlank(rejectDepartmentId)) {
			params.put("rejectDepartmentId", rejectDepartmentId);
		}
		String rejectDepartmentName = reject.getRejectDepartmentName();
		if(CommonUtil.isNotBlank(rejectDepartmentName)) {
			params.put("rejectDepartmentName", rejectDepartmentName);
		}
		String simpleCode = reject.getSimpleCode();
		if(CommonUtil.isNotBlank(simpleCode)) {
			params.put("simpleCode", simpleCode);
		}
		String simpleBarCode = reject.getSimpleBarCode();
		if(CommonUtil.isNotBlank(simpleBarCode)) {
			params.put("simpleBarCode", simpleBarCode);
		}
		String receiptCode = reject.getReceiptCode();
		if(CommonUtil.isNotBlank(receiptCode)) {
			params.put("receiptCode", receiptCode);
		}
		String simpleName = reject.getSimpleName();
		if(CommonUtil.isNotBlank(simpleName)) {
			params.put("simpleName", simpleName);
		}
		return params;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void rejectAlarmListExcel(CommonDTO common, RejectModel reject, HttpServletResponse response) throws Exception {
		Map<String, Object> params = buildQueryParamsMap(reject);
		if (common == null || StringUtils.isBlank(common.getStartDate()) || StringUtils.isBlank(common.getEndDate())) {
			throw new DataException("534");
		}
		params.put("startDate", common.getStartDate());
		params.put("endDate", common.getEndDate());
		List<Map<String, Object>> rejectReportList = queryListByObject(QueryId.QUERY_REJECT_ALARM_LIST_FOR_REPORT, params);
		for(Map<String, Object> map : rejectReportList) {
			String discountAlarmFlag = (String) map.get("discountAlarmFlag");
			map.put("alarmFlag", CommonUtil.isNotBlank(discountAlarmFlag) ? discountAlarmFlag : (String) map.get("contractAlarmFlag"));
		}
		List<String> codeList = codeDictService
				.queryCodeListByServiceCode(CodeEnum.CODE_DICT_REJECT_ALARM_REPORT.value());
		String title = "退单警报报表";
		ExcelUtil excelUtil = new ExcelUtil();
		String fileName = "退单警报报表-" + DateUtil.getCurrentDateStr();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
		excelUtil.exportTemplateByMap(CommonValue.REJECT_ALARM_REPORT_HEADER, rejectReportList, title, codeList, response.getOutputStream());
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil uploadRejectData(MultipartFile file) throws Exception {
		ExcelUtil<Reject> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> rejectMapList = excelUtil.getExcelList(file, ExcelEnum.REJECT_TEMPLATE_TYPE.value());
		if (rejectMapList == null) {
			return ResultUtil.error("格式不符，导入失败");
		}
		insert(InsertId.INSERT_BATCH_REJECT, rejectMapList);
		return ResultUtil.success();
	}
}
