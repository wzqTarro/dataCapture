package com.data.service;

import java.util.List;

import com.data.bean.PromotionStoreList;
import com.data.utils.ResultUtil;

public interface IPromotionStoreListService {

	/**
	 * 保存促销生效门店
	 * @param promotionDetailId 
	 * @param storeList
	 * @return
	 */
	ResultUtil savePromotionStoreList(Integer promotionDetailId, List<PromotionStoreList> storeList);
	/**
	 * 获取促销门店列表
	 * @param promotionId
	 * @return
	 */
	ResultUtil getPromotionStoreList(Integer promotionDetailId);
}
