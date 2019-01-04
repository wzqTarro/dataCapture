package com.data.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONPath;
import com.data.bean.PromotionDetail;
import com.data.bean.PromotionStoreList;
import com.data.bean.TemplateStore;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.service.IPromotionStoreListService;
import com.data.service.IRedisService;
import com.data.utils.ResultUtil;

import io.netty.util.internal.StringUtil;

@Service
public class PromotionStoreListServiceImpl extends CommonServiceImpl implements IPromotionStoreListService{

	@Autowired
	private IRedisService redisService;
	
	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil savePromotionStoreList(Integer promotionDetailId, List<PromotionStoreList> storeList) {
		if (promotionDetailId == null || promotionDetailId == 0) {
			return ResultUtil.error("促销明细ID不能为空");
		}
		PromotionDetail promotionDetail = (PromotionDetail) queryObjectByParameter(QueryId.QUERY_PROMOTION_DETAIL_INFO_BY_ID, promotionDetailId);
		if (promotionDetail != null) {
			String sysId = promotionDetail.getSysId();
			List<TemplateStore> allStoreList = redisService.queryTemplateStoreList();
			allStoreList = (List<TemplateStore>) JSONPath.eval(allStoreList, "$[sysId='"+ sysId +"']");
			int allNum = allStoreList.size();
			int storeNum = storeList.size();
			if (allNum == storeNum) {
				promotionDetail.setStoreName("全系统");
				update(UpdateId.UPDATE_PROMOTION_DETAIL_BY_MESSAGE, promotionDetail);
			} else {
				promotionDetail.setStoreName("部分门店");
				update(UpdateId.UPDATE_PROMOTION_DETAIL_BY_MESSAGE, promotionDetail);
			}
			if (!CollectionUtils.isEmpty(storeList)) {			
				Set<String> storeIdSet = new HashSet<>();
				for (int i = 0; i < storeList.size(); i++) {
					PromotionStoreList store = storeList.get(i);
					storeIdSet.add(store.getStoreCode());
				}
				if (storeIdSet.size() != storeList.size()) {
					return ResultUtil.error("门店编号不能重复");
				}
				delete(DeleteId.DELETE_PROMOTION_STORE_LIST_BY_DETAIL_ID, promotionDetailId);
				insert(InsertId.INSERT_PROMOTION_STORE_LIST_BATCH, storeList);
			} else {
				delete(DeleteId.DELETE_PROMOTION_STORE_LIST_BY_DETAIL_ID, promotionDetailId);
			}
			
		} else {
			return ResultUtil.error("促销明细ID有误");
		}
		return ResultUtil.success();
	}
	@Override
	public ResultUtil getPromotionStoreList(Integer promotionDetailId) {
		if (promotionDetailId == null || promotionDetailId == 0) {
			return ResultUtil.error("促销ID不能为空");
		}
		List<PromotionStoreList> storeList = queryListByObject(QueryId.QUERY_PROMOTION_STORE_LIST_BY_DETAIL_ID, promotionDetailId);
		return ResultUtil.success(storeList);
	}
}
