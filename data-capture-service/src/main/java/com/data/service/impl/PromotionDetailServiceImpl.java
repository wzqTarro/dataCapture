package com.data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.data.bean.PromotionDetail;
import com.data.bean.PromotionStoreList;
import com.data.bean.TemplateStore;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.ExcelEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.service.IPromotionDetailService;
import com.data.utils.CommonUtil;
import com.data.utils.DateUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

@Service
public class PromotionDetailServiceImpl extends CommonServiceImpl implements IPromotionDetailService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ResultUtil getPromotionDetailByCondition(CommonDTO common, String sysId, String productCode, Integer page,
			Integer limit) throws Exception {
		logger.info("------->>>>>>>>查詢時間common:{}<<<<<<<<--------", FastJsonUtil.objectToString(common));
		logger.debug("------>>>>>>系統編號sysId{}, 條碼:simpleBarCode{}, 分頁page:{}, limit:{}<<<<<<-------", 
				sysId, productCode, page, limit);
		Map<String, Object> param = new HashMap<>();
		if (common != null && CommonUtil.isNotBlank(common.getStartDate()) && CommonUtil.isNotBlank(common.getEndDate())) {
			param.put("startDate", common.getStartDate());
			param.put("endDate", common.getEndDate());
		}
		if (StringUtils.isNoneBlank(sysId)) {
			param.put("sysId", sysId);
		}
		if (StringUtils.isNoneBlank(productCode)) {
			param.put("productCode", productCode);
		}
		PageRecord<PromotionDetail> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_PROMOTION_DETAIL_BY_PARAM, QueryId.QUERY_PROMOTION_DETAIL_BY_PARAM, param, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil insertPromotionDetail(PromotionDetail promotionDetail, String supplyPriceStartTime,
			String supplyPriceEndTime, String sellPriceStartTime, String sellPriceEndTime) {
		logger.info("-------->>>>>>>>更新促銷promotionDetail:{}<<<<<<<<---------", FastJsonUtil.objectToString(promotionDetail));
		logger.info("------>>>>>>>>>supplyPriceStartTime:{}, supplyPriceEndTime:{}, sellPriceStartTime:{}, sellPriceEndTime:{}<<<<<<<<<<-------", 
				supplyPriceStartTime, supplyPriceEndTime, sellPriceStartTime, sellPriceEndTime);
		if (promotionDetail == null) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		promotionDetail.setId(null);
		try {
			promotionDetail.setSellPriceEndDate(DateUtil.stringToDate(sellPriceEndTime));
			promotionDetail.setSellPriceStartDate(DateUtil.stringToDate(sellPriceStartTime));
			promotionDetail.setSupplyPriceEndDate(DateUtil.stringToDate(supplyPriceEndTime));
			promotionDetail.setSupplyPriceStartDate(DateUtil.stringToDate(supplyPriceStartTime));
		} catch (Exception e) {
			return ResultUtil.error("日期格式错误,格式:2018-11-12");
		}
		insert(InsertId.INSERT_PROMOTION_DETAIL_BY_MESSAGE, promotionDetail);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil updatePromotionDetail(PromotionDetail promotionDetail, String supplyPriceStartTime,
			String supplyPriceEndTime, String sellPriceStartTime, String sellPriceEndTime) {
		logger.info("-------->>>>>>>>更新促銷promotionDetail:{}<<<<<<<<---------", FastJsonUtil.objectToString(promotionDetail));
		logger.info("------>>>>>>>>>supplyPriceStartTime:{}, supplyPriceEndTime:{}, sellPriceStartTime:{}, sellPriceEndTime:{}<<<<<<<<<<-------", 
				supplyPriceStartTime, supplyPriceEndTime, sellPriceStartTime, sellPriceEndTime);
		if (promotionDetail == null || promotionDetail.getId() == null) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		Map<String, Object> param = new HashedMap<>(1);
		param.put("id", promotionDetail.getId());
		int count = queryCountByObject(QueryId.QUERY_COUNT_PROMOTION_DETAIL_BY_PARAM, param);
		if (count == 0) {
			return ResultUtil.error(TipsEnum.ID_NOT_EXIST.getValue());
		}
		promotionDetail.setSellPriceEndDate(DateUtil.stringToDate(sellPriceEndTime));
		promotionDetail.setSellPriceStartDate(DateUtil.stringToDate(sellPriceStartTime));
		promotionDetail.setSupplyPriceEndDate(DateUtil.stringToDate(supplyPriceEndTime));
		promotionDetail.setSupplyPriceStartDate(DateUtil.stringToDate(supplyPriceStartTime));
		update(UpdateId.UPDATE_PROMOTION_DETAIL_BY_MESSAGE, promotionDetail);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil deletePromotionDetail(int id) {
		logger.info("--------->>>>>>id:{}<<<<<------------", id);
		if (id == 0) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		Map<String, Object> param = new HashedMap<>(1);
		param.put("id", id);
		int count = queryCountByObject(QueryId.QUERY_COUNT_PROMOTION_DETAIL_BY_PARAM, param);
		if (count == 0) {
			return ResultUtil.error(TipsEnum.ID_NOT_EXIST.getValue());
		}
		delete(DeleteId.DELETE_PROMOTION_DETAIL_BY_ID, id);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil queryPromotionInfo(String id) {
		if(CommonUtil.isNotBlank(id)) {
			Map<String, Object> params = new HashMap<>(4);
			params.put("id", Integer.parseInt(id));
			PromotionDetail detail = (PromotionDetail) queryObjectByParameter(QueryId.QUERY_PROMOTION_DETAIL_INFO_BY_ID, params);
			return ResultUtil.success(detail);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil uploadPromotionDetailData(MultipartFile file) throws Exception {
		ExcelUtil<PromotionDetail> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> promotionDetailMapList = excelUtil.getExcelList(file, ExcelEnum.PROMOTION_DETAIL_TEMPLATE_TYPE.value());
		if (promotionDetailMapList == null) {
			return ResultUtil.error("格式不符，导入失败");
		}
		for (int i = 0; i < promotionDetailMapList.size(); i++) {
			Map<String, Object> promotionMap = promotionDetailMapList.get(i);
			
			// 生效门店
			String effectiveStore = (String) promotionMap.get("effectiveStore");
			
			// 除外门店
			String exceptStore = (String) promotionMap.get("exceptStore");
			if (StringUtils.isBlank(effectiveStore)) {
				return ResultUtil.error("生效门店列不能为空");
			}
			
			// 生效门店数组
			String[] effectiveStoreArray = CommonUtil.parseIdsCollection(effectiveStore, ";");			
			
			PromotionDetail promotion = JSON.parseObject(JSON.toJSONString(promotionMap), PromotionDetail.class);
			// 插入促销明细
			insert(InsertId.INSERT_PROMOTION_DETAIL_BY_MESSAGE, promotion);
			
			String sysId = promotion.getSysId();
			
			List<PromotionStoreList> effectiveStoreList = new ArrayList<>();
			
			// 插入生效门店
			if ("全系统".equals(effectiveStore)) {

				promotion.setStoreName("全系统");
				
				Map<String, Object> paramMap = new HashMap<>(1);
				paramMap.put("sysId", sysId);
				
				// 全部门店
				List<TemplateStore> allStoreList = queryListByObject(QueryId.QUERY_STORE_BY_PARAM, paramMap);
				
				for (int j = 0; j < allStoreList.size(); j++) {
					TemplateStore store = allStoreList.get(j);
					String effectiveStoreCode = store.getStoreCode();
					
					PromotionStoreList promotionStore = new PromotionStoreList();
					promotionStore.setPromotionDetailId(promotion.getId());
					promotionStore.setStoreCode(effectiveStoreCode);
					
					effectiveStoreList.add(promotionStore);
				}
				
			} else {
				promotion.setStoreName("部分门店");
				// 部分生效门店
				for (int j = 0; j < effectiveStoreArray.length; j++) {
					String effectiveStoreCode = effectiveStoreArray[j];
					
					PromotionStoreList promotionStore = new PromotionStoreList();
					promotionStore.setPromotionDetailId(promotion.getId());
					promotionStore.setStoreCode(effectiveStoreCode);
					
					effectiveStoreList.add(promotionStore);
				}
			}
			
			update(UpdateId.UPDATE_PROMOTION_DETAIL_BY_MESSAGE, promotion);
			insert(InsertId.INSERT_PROMOTION_STORE_LIST_BATCH, effectiveStoreList);
			
			// 除外门店数组
			if (StringUtils.isNoneBlank(exceptStore)) {
				String[] exceptStoreArray = CommonUtil.parseIdsCollection(exceptStore, ";");
				
				for (int j = 0; j < exceptStoreArray.length; j++) {
					String exceptStoreCode = exceptStoreArray[j];
					
					Map<String, Object> paramMap = new HashMap<>(2);
					paramMap.put("promotionDetailId", promotion.getId());
					paramMap.put("storeCode", exceptStoreCode);
					
					delete(DeleteId.DELETE_PROMOTION_STORE_LIST_BY_DETAIL_ID_AND_STORE_CODE, paramMap);
				}
				
			}
			
		}
		//insert(InsertId.INSERT_BATCH_PROMOTION_DETAIL, promotionDetailMapList);
		return ResultUtil.success();
	}

}
