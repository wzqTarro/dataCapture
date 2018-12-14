package com.data.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONPath;
import com.data.bean.PromotionStoreList;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.service.IPromotionStoreListService;
import com.data.utils.ResultUtil;

@Service
public class PromotionStoreListServiceImpl extends CommonServiceImpl implements IPromotionStoreListService{

	@Override
	public ResultUtil savePromotionStoreList(List<PromotionStoreList> storeList) {
		if (CollectionUtils.isEmpty(storeList)) {
			return ResultUtil.error("门店不能为空");
		}
		Set<String> storeIdSet = new HashSet<>();
		for (int i = 0; i < storeList.size(); i++) {
			PromotionStoreList store = storeList.get(i);
			storeIdSet.add(store.getStoreCode());
		}
		if (storeIdSet.size() != storeList.size()) {
			return ResultUtil.error("门店编号不能重复");
		}
		Integer promotionId = (Integer) JSONPath.eval(storeList, "$[0].promotionDetailId");
		delete(DeleteId.DELETE_PROMOTION_STORE_LIST_BY_DETAIL_ID, promotionId);
		insert(InsertId.INSERT_PROMOTION_STORE_LIST_BATCH, storeList);
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
