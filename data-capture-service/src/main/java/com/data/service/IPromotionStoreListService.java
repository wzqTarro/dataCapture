package com.data.service;

import java.util.List;

import com.data.bean.PromotionStoreList;
import com.data.utils.ResultUtil;

public interface IPromotionStoreListService {

	/**
	 * 保存促销生效门店
	 * @param storeList
	 * @return
	 */
	ResultUtil savePromotionStoreList(List<PromotionStoreList> storeList);
}
