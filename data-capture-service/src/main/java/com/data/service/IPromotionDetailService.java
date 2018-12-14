package com.data.service;

import org.springframework.web.multipart.MultipartFile;

import com.data.bean.PromotionDetail;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface IPromotionDetailService {

	/**
	 * 多条件分页查询
	 * @param common
	 * @param sysId
	 * @param simpleBarCode
	 * @return
	 */
	ResultUtil getPromotionDetailByCondition(CommonDTO common, String sysId, String productCode, Integer page, Integer limit) throws Exception ;
	/**
	 * 插入
	 * @param promotionDetail
	 * @return
	 */
	ResultUtil insertPromotionDetail(PromotionDetail promotionDetail, String supplyPriceStartTime, String supplyPriceEndTime, 
			String sellPriceStartTime, String sellPriceEndTime);
	/**
	 * 更新
	 * @param promotionDetail
	 * @return
	 */
	ResultUtil updatePromotionDetail(PromotionDetail promotionDetail, String supplyPriceStartTime, String supplyPriceEndTime, 
			String sellPriceStartTime, String sellPriceEndTime);
	/**
	 * 删除
	 * @param promotionDetail
	 * @return
	 */
	ResultUtil deletePromotionDetail(int id);
	
	/**
	 * 按id查询促销明细信息
	 * @param id
	 * @return
	 */
	ResultUtil queryPromotionInfo(String id);
	
	/**
	 * 促销明细导入
	 * @param file
	 * @return
	 * @throws Exception
	 */
	ResultUtil uploadPromotionDetailData(MultipartFile file) throws Exception;
}
