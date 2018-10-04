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

import com.data.bean.Reject;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.RejectEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
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

	@Override
	public ResultUtil getRejectByWeb(String queryDate, String sysId, Integer limit) {
		PageRecord<Reject> pageRecord = null;
		logger.info("------>>>>>>开始抓取退单数据<<<<<<---------");
		
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		
		Map<String, Object> queryParam = new HashMap<>(2);
		queryParam.put("queryDate", queryDate);
		queryParam.put("sysId", sysId);
		int count = queryCountByObject(QueryId.QUERY_COUNT_REJECT_BY_PARAM, queryParam);
		
		logger.info("------>>>>>>count:{}<<<<<<-------", count);
		List<Reject> rejectList = null;
		
		if (count == 0) {
			try {
				rejectList = dataCaptureUtil.getDataByWeb(queryDate, sysId, WebConstant.REJECT, Reject.class);
			} catch (IOException e) {
				return ResultUtil.error(TipsEnum.GRAB_DATA_ERROR.getValue());
			}
			List<TemplateStore> storeList = redisService.queryTemplateStoreList();
			List<TemplateProduct> productList = redisService.queryTemplateProductList();
			for (int i = 0, size = rejectList.size(); i < size; i++) {
				Reject reject = rejectList.get(i);
				
				// 系统名称
				String sysName = reject.getSysName();
				
				// 单品编码
				String simpleCode = reject.getSimpleCode();
				
				// 单品条码
				String simpleBarCode = reject.getSimpleBarCode();
				
				// 门店编码
				String storeCode = reject.getRejectDepartmentId();
				
				// 条码信息
				simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
				if (CommonUtil.isBlank(simpleBarCode)) {
					reject.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
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
					reject.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
					continue;
				}
				
				// 单品条码
				reject.setSimpleBarCode(simpleBarCode);
				
				// 单品名称
				reject.setSimpleName(product.getStandardName());
					
				// 库存编号
				reject.setStockCode(product.getStockCode());
				
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
			dataCaptureUtil.insertData(rejectList, InsertId.INSERT_BATCH_REJECT);
		} else {
			rejectList = queryListByObject(QueryId.QUERY_REJECT_BY_PARAM, queryParam);
		}
		pageRecord = dataCaptureUtil.setPageRecord(rejectList, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getRejectByParam(CommonDTO common, Reject reject, Integer page, Integer limit) throws Exception {
		logger.info("--->>>订单查询参数common: {}<<<---", FastJsonUtil.objectToString(common));
		logger.info("---->>>reject:{}<<<------", FastJsonUtil.objectToString(reject));
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
		}
		PageRecord<Reject> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_REJECT_BY_PARAM, QueryId.QUERY_REJECT_BY_PARAM, map, page, limit);
		logger.info("--->>>订单查询结果分页: {}<<<---", FastJsonUtil.objectToString(pageRecord));
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

		// 门店编号
		if (CommonUtil.isNotBlank(reject.getRejectDepartmentId())) {
			param.put("rejectDepartmentId", reject.getRejectDepartmentId());
		}

		// 单据号码
		if (CommonUtil.isNotBlank(reject.getReceiptCode())) {
			param.put("receiptCode", reject.getReceiptCode());
		}
		List<Reject> dataList = queryListByObject(QueryId.QUERY_REJECT_BY_ANY_COLUMN, param);

		ExcelUtil<Reject> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007("退单信息", header, methodNameArray, dataList, output);
	}
}
