package com.data.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONPath;
import com.data.bean.PromotionStoreList;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.service.IPromotionStoreListService;
import com.data.utils.ResultUtil;

import io.netty.util.internal.StringUtil;

@Service
public class PromotionStoreListServiceImpl extends CommonServiceImpl implements IPromotionStoreListService{

	@Override
	public ResultUtil savePromotionStoreList(Integer promotionDetailId, List<PromotionStoreList> storeList) {
		if (promotionDetailId == null || promotionDetailId == 0) {
			return ResultUtil.error("促销明细ID不能为空");
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
