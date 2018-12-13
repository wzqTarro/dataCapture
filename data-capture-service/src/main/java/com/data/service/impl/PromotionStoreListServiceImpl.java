package com.data.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONPath;
import com.data.bean.PromotionStoreList;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.service.IPromotionStoreListService;
import com.data.utils.ResultUtil;

@Service
public class PromotionStoreListServiceImpl extends CommonServiceImpl implements IPromotionStoreListService{

	@Override
	public ResultUtil savePromotionStoreList(List<PromotionStoreList> storeList) {
		if (CollectionUtils.isEmpty(storeList)) {
			return ResultUtil.error("门店不能为空");
		}
		Integer promotionId = (Integer) JSONPath.eval(storeList, "$[0].promotionId");
		delete(DeleteId.DELETE_PROMOTION_STORE_LIST_BY_DETAIL_ID, promotionId);
		insert(InsertId.INSERT_PROMOTION_STORE_LIST_BATCH, storeList);
		return ResultUtil.success();
	}

}
