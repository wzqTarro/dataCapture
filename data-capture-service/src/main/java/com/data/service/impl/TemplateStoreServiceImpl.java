package com.data.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.TemplateStore;
import com.data.constant.PageRecord;
import com.data.constant.RedisAPI;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.TipsEnum;
import com.data.model.RegionMenu;
import com.data.service.ITemplateStoreService;
import com.data.utils.CommonUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.RedisUtil;
import com.data.utils.ResultUtil;

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
		Map<String, Object> param = new HashMap<>(1);
		param.put("sysId", sysId);
		List<RegionMenu> storeList = queryListByObject(QueryId.QUERY_REGION_MENU, param);
		return ResultUtil.success(storeList);
	}

	@Override
	public ResultUtil getStoreMenu(String sysId) {
		Map<String, Object> param = new HashMap<>(1);
		param.put("sysId", sysId);
		List<Map<String, Object>> storeList = queryListByObject(QueryId.QUERY_STORE_MENU, param);
		return ResultUtil.success(storeList);
	}

	@Override
	public ResultUtil queryStoreInfo(String id) {
		if(CommonUtil.isNotBlank(id)) {
			Map<String, Object> params = new HashMap<>(4);
			params.put("id", Integer.parseInt(id));
			TemplateStore store = (TemplateStore) queryObjectByParameter(QueryId.QUERY_STORE_BY_ID, params);
			return ResultUtil.success(store);
		}
		return null;
	}

	@Override
	public ResultUtil importStoreExcel(MultipartFile file) throws IOException {
		ExcelUtil<TemplateStore> excelUtil = new ExcelUtil<>();
		String[] headers = new String[]{"系统编号","门店编码","供应链订单店称","供应链退单店称","供应链销量店称","百亚标准门店名称","门店所在市场",
				"门店所在城市","门店系统","门店负责人","物流模式","开业时间","配送商编码","配送商名称","配送责任人","大区","省区","归属","业绩归属"};
		List<Map<String, Object>> storeMapList = excelUtil.getExcelList(file, headers);
		if (storeMapList == null) {
			return ResultUtil.error("格式不符，导入失败");
		}
		
		
		
		TemplateStore store = null;
		List<TemplateStore> storeList = new ArrayList<>();
		Map<String, Object> map = null;
		
		for (int i = 0, size = storeMapList.size(); i < size; i++) {
			map = storeMapList.get(i);
			store = new TemplateStore();
			store.setSysId(String.valueOf(map.get("系统编号")));
			store.setSysName((String)map.get("门店系统"));
			store.setStoreCode((String)map.get("门店编码"));
			store.setOrderStoreName((String)map.get("供应链订单店称"));
			store.setReturnStoreName((String)map.get("供应链退单店称"));
			store.setSaleStoreName((String)map.get("供应链销量店称"));
			store.setStandardStoreName((String)map.get("百亚标准门店名称"));
			store.setStoreMarket((String)map.get("门店所在市场"));
			store.setStoreCity((String)map.get("门店所在城市"));
			store.setStoreManager((String)map.get("门店负责人"));
			store.setLogisticsModel((String)map.get("物流模式"));
			store.setPracticeTime((map.get("开业时间") != null && "".equals(map.get("开业时间")))?(Date)map.get("开业时间"):Date.from(LocalDateTime.MIN.toInstant(ZoneOffset.of("+8"))));
			store.setDistributionCode((String)map.get("配送商编码"));
			store.setDistributionName((String)map.get("配送商名称"));
			store.setDistributionUser((String)map.get("配送责任人"));
			store.setRegion((String)map.get("大区"));
			store.setProvinceArea((String)map.get("省区"));
			store.setAscription((String)map.get("归属"));
			store.setAscriptionSole((String)map.get("业绩归属"));
			storeList.add(store);
		}
		logger.info(FastJsonUtil.objectToString(storeList));
		//insert(InsertId.INSERT_BATCH_STORE, storeList);
		return ResultUtil.success();
	}
}
