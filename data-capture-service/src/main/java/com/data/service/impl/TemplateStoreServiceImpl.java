package com.data.service.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.TemplateStore;
import com.data.constant.PageRecord;
import com.data.constant.RedisAPI;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.TipsEnum;
import com.data.service.ITemplateStoreService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.RedisUtil;
import com.data.utils.ResultUtil;
import com.data.utils.StreamUtil;

@Service
public class TemplateStoreServiceImpl extends CommonServiceImpl implements ITemplateStoreService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ResultUtil getTemplateStoreByParam(TemplateStore store, Integer page, Integer limit) throws Exception {
		logger.info("------>>>>>参数：{}<<<<<<-------", FastJsonUtil.objectToString(store));
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
			
			// 系统名称
			if (CommonUtil.isNotBlank(store.getSysName())) {
				map.put("sysName", store.getSysName());
			}
			
		}
		logger.info("------>>>>>模板门店查询条件:{}<<<<<------", FastJsonUtil.objectToString(map));
		PageRecord<TemplateStore> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_STORE_BY_PARAM, 
				QueryId.QUERY_STORE_BY_PARAM, map, page, limit);
		logger.info("------>>>>>模板门店分页信息:{}<<<<<-------", FastJsonUtil.objectToString(pageRecord));
		return ResultUtil.success(pageRecord);
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil updateTemplateStore(TemplateStore templateStore, String practiceDate)  throws ParseException {
		logger.info("----->>>>参数：{}<<<<-----", FastJsonUtil.objectToString(templateStore));
		if (null == templateStore) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		if (null == templateStore.getId()) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		if (CommonUtil.isNotBlank(practiceDate)) {
			templateStore.setPracticeTime(DateUtils.parseDate(practiceDate, "yyyy-MM-dd"));
		}
		update(UpdateId.UPDATE_STORE_BY_MESSAGE, templateStore);
		RedisUtil.del(RedisAPI.STORE_TEMPLATE);
		return ResultUtil.success();
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil insertTemplateStore(TemplateStore templateStore, String practiceDate) throws ParseException {
		logger.info("----->>>>参数：{}<<<<-----", FastJsonUtil.objectToString(templateStore));
		if (null == templateStore) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		templateStore.setId(null);
		if (CommonUtil.isBlank(practiceDate)) {
			return ResultUtil.error(TipsEnum.PRAC_DATE_IS_NULL.getValue());
		}
		templateStore.setPracticeTime(DateUtils.parseDate(practiceDate, "yyyy-MM-dd"));
		insert(InsertId.INSERT_STORE_BY_MESSAGE, templateStore);
		RedisUtil.del(RedisAPI.STORE_TEMPLATE);
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
		RedisUtil.del(RedisAPI.STORE_TEMPLATE);
		return ResultUtil.success();
	}
	@Override
	public ResultUtil getRegionMenu(String sysId) {
		if (CommonUtil.isBlank(sysId)) {
			return ResultUtil.error(TipsEnum.SYS_ID_IS_NULL.getValue());
		}
		Map<String, Object> param = new HashMap<>(1);
		param.put("sysId", sysId);
		List<TemplateStore> storeList = queryListByObject(QueryId.QUERY_STORE_BY_ANY_COLUMN, param);
		Map<String, List<TemplateStore>> regionMap = storeList.parallelStream()
				.filter(StreamUtil.distinctByKey(TemplateStore::getProvinceArea))
				.collect(Collectors.groupingBy(TemplateStore::getRegion));
		
		return ResultUtil.success(regionMap);
	}
}
