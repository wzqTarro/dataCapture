package com.data.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.TemplateSupply;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.ExcelEnum;
import com.data.constant.enums.TipsEnum;
import com.data.service.ITemplateSupplyService;
import com.data.utils.CommonUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.google.common.collect.Maps;

@Service
public class TemplateSupplyServiceImpl extends CommonServiceImpl implements ITemplateSupplyService {

	private static Logger logger = LoggerFactory.getLogger(TemplateSupplyServiceImpl.class);

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil insertSupply(TemplateSupply supply) {
		if (null == supply) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		logger.info("----->>>>>>supply:"+ FastJsonUtil.objectToString(supply) +"<<<<<<-------");
		// 区域
		if (StringUtils.isBlank(supply.getRegion())) {
			return ResultUtil.error(TipsEnum.REGION_IS_NULL.getValue());
		}
		
		// 系统名
		if (StringUtils.isBlank(supply.getSysName())) {
			return ResultUtil.error(TipsEnum.SYS_NAME_IS_NULL.getValue());
		}
		
		// 链接
		if (StringUtils.isBlank(supply.getUrl())) {
			return ResultUtil.error(TipsEnum.SYS_URL_IS_NULL.getValue());
		}
		
		// 登录账号
		if (StringUtils.isBlank(supply.getLoginUserName())) {
			return ResultUtil.error(TipsEnum.SYS_LOGIN_USER_NAME_IS_NULL.getValue());
		}
		
		// 登录密码
		if (StringUtils.isBlank(supply.getLoginPassword())) {
			return ResultUtil.error(TipsEnum.SYS_LOGIN_PWD_IS_NULL.getValue());
		}
		
		// 供应链默认无效
		supply.setIsVal(false);
		
		// 供应链接口
		supply.setControllerName("");
		
		// 系统编号
		supply.setSysId(CommonUtil.createWorkNo());
		return ResultUtil.success(insert(InsertId.INSERT_NEW_SUPPLY_MESSAGE, supply));
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateSupply(TemplateSupply supply) {
		if (null == supply) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		
		// 供应链ID
		if (null == supply.getId()) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		logger.info("----->>>>>>supply:"+ FastJsonUtil.objectToString(supply) +"<<<<<<-------");
		return ResultUtil.success(update(UpdateId.UPDATE_SUPPLY_MESSAGE, supply));
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil deleteSupply(Integer id) {
		if (null == id) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		logger.info("----->>>>>>id:"+ id +"<<<<<<-------");
		return ResultUtil.success(delete(DeleteId.DELETE_SUPPLY_BY_ID, id));
	}

	@Override
	public ResultUtil querySupplyByConditiion(TemplateSupply supply, Integer page, Integer limit) throws Exception {
		logger.info("----->>>>>supply:{}<<<<<------", FastJsonUtil.objectToString(supply));
		logger.info("----->>>>>page:{}, limit:{}<<<<<------", page, limit);
		Map<String, Object> map = Maps.newHashMap();
		if (null != supply) {
			
			// 供应链名称
			if (StringUtils.isNoneBlank(supply.getSysName())) {
				map.put("sysName", supply.getSysName());
			}
			
			// 供应链区域
			if (StringUtils.isNoneBlank(supply.getRegion())) {
				map.put("region", supply.getRegion());
			}
		}
		logger.info("------>>>>>供应链查询条件:{}<<<<<------", FastJsonUtil.objectToString(map));
		PageRecord<TemplateSupply> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_SUPPLY_BY_CONDITION, 
				QueryId.QUERY_SUPPLY_BY_CONDITION, map, page, limit);
		logger.info("---->>>>>供应链分页结果:"+ FastJsonUtil.objectToString(pageRecord) +"<<<<<<--------");
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getSupplyMenu() {
		Map<String, Object> param = new HashMap<>(2);
		param.put("column", " DISTINCT sys_id as sysId, sys_name as sysName, region ");
		param.put("isVal", 1);
		List<Map<String, Object>> supplyList = queryListByObject(QueryId.QUERY_SUPPLY_ANY_COLUMN, param);
		return ResultUtil.success(supplyList);
	}

	@Override
	public ResultUtil querySupplyInfo(String id) {
		if(CommonUtil.isNotBlank(id)) {
			Map<String, Object> params = new HashMap<>(1);
			params.put("id", Integer.parseInt(id));
			TemplateSupply supply = (TemplateSupply) queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_CONDITION, params);
			return ResultUtil.success(supply);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil uploadTemplateSupplyData(MultipartFile file) throws Exception {
		ExcelUtil<TemplateSupply> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> templateSupplyMapList = excelUtil.getExcelList(file, ExcelEnum.TEMPLATE_SUPPLY_TEMPLATE_TYPE.value());
		if (templateSupplyMapList == null) {
			return ResultUtil.error("格式不符，导入失败");
		}
		insert(InsertId.INSERT_BATCH_ORDER, templateSupplyMapList);
		return ResultUtil.success();
	}
}
