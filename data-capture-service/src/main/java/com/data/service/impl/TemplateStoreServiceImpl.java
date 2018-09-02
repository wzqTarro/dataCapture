package com.data.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.TemplateStore;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.dto.CommonDTO;
import com.data.service.ITemplateStoreService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

@Service
public class TemplateStoreServiceImpl extends CommonServiceImpl implements ITemplateStoreService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ResultUtil getTemplateStoreByParam(String param) throws Exception {
		logger.info("------>>>>>参数：{}<<<<<<-------", param);
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		TemplateStore store = FastJsonUtil.jsonToObject(param, TemplateStore.class);
		if (null == common) {
			common = new CommonDTO();
		}
		Map<String, Object> map = null;
		if (null != store) {
			map = new HashMap<>(5);
			
			// 大区
			if (CommonUtil.isNotBlank(store.getRegion())) {
				map.put("region", store.getRegion());
			}
			
			// 省区
			if (CommonUtil.isNotBlank(store.getProvinceArea())) {
				map.put("provinceArea", store.getProvinceArea());
			}
			
			// 配送商名称
			if (CommonUtil.isNotBlank(store.getDistributionName())) {
				map.put("distributionName", store.getDistributionName());
			}
			
			// 配送商编码
			if (CommonUtil.isNotBlank(store.getDistributionCode())) {
				map.put("distributionCode", store.getDistributionCode());
			}
			
			// 门店编号
			if (CommonUtil.isNotBlank(store.getStoreCode())) {
				map.put("storeCode", store.getStoreCode());
			}
			
		}
		logger.info("------>>>>>模板门店查询条件:{}<<<<<------", FastJsonUtil.objectToString(map));
		PageRecord<TemplateStore> page = queryPageByObject(QueryId.QUERY_COUNT_STORE_BY_PARAM, 
				QueryId.QUERY_STORE_BY_PARAM, map, common.getPage(), common.getLimit());
		logger.info("------>>>>>模板门店分页信息:{}<<<<<-------", FastJsonUtil.objectToString(page));
		return ResultUtil.success(page);
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil updateTemplateStore(TemplateStore templateStore) {
		logger.info("----->>>>参数：{}<<<<-----", FastJsonUtil.objectToString(templateStore));
		if (null == templateStore) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		if (null == templateStore.getId()) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		update(UpdateId.UPDATE_STORE_BY_MESSAGE, templateStore);
		return ResultUtil.success();
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil insertTemplateStore(TemplateStore templateStore) {
		logger.info("----->>>>参数：{}<<<<-----", FastJsonUtil.objectToString(templateStore));
		if (null == templateStore) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		templateStore.setId(null);
		insert(InsertId.INSERT_STORE_BY_MESSAGE, templateStore);
		return ResultUtil.success();
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil deleteTemplateStoreById(int id) {
		logger.info("------>>>>>>参数：id:{}<<<<<<-----", id);
		if (id == 0) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		delete(DeleteId.DELETE_STORE_BY_ID, id);
		return ResultUtil.success();
	}
}
