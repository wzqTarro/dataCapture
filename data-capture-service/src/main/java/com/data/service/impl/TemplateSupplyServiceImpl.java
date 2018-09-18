package com.data.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.TemplateSupply;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.dto.CommonDTO;
import com.data.service.ITemplateSupplyService;
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
		if (StringUtils.isNoneBlank(supply.getRegion())) {
			return ResultUtil.error(TipsEnum.SYS_REGION_IS_NULL.getValue());
		}
		
		// 系统名
		if (StringUtils.isNoneBlank(supply.getSysName())) {
			return ResultUtil.error(TipsEnum.SYS_NAME_IS_NULL.getValue());
		}
		
		// 链接
		if (StringUtils.isNoneBlank(supply.getUrl())) {
			return ResultUtil.error(TipsEnum.SYS_URL_IS_NULL.getValue());
		}
		
		// 登录账号
		if (StringUtils.isNoneBlank(supply.getLoginUserName())) {
			return ResultUtil.error(TipsEnum.SYS_LOGIN_USER_NAME_IS_NULL.getValue());
		}
		
		// 登录密码
		if (StringUtils.isNoneBlank(supply.getLoginPassword())) {
			return ResultUtil.error(TipsEnum.SYS_LOGIN_PWD_IS_NULL.getValue());
		}
		
		// 供应链默认无效
		supply.setIsVal(false);
		
		// 供应链接口
		supply.setControllerName("");
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
	public ResultUtil querySupplyByConditiion(String param) throws Exception {
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		TemplateSupply supply = FastJsonUtil.jsonToObject(param, TemplateSupply.class);
		logger.info("----->>>>>common:"+ FastJsonUtil.objectToString(common) +"<<<<<------");
		logger.info("----->>>>>supply:"+ FastJsonUtil.objectToString(supply) +"<<<<<------");
		if (null == common) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		} 
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
		PageRecord<TemplateSupply> page = queryPageByObject(QueryId.QUERY_COUNT_SUPPLY_BY_CONDITION, QueryId.QUERY_SUPPLY_BY_CONDITION, map, common.getPage(), common.getLimit());
		logger.info("---->>>>>供应链分页结果:"+ FastJsonUtil.objectToString(page) +"<<<<<<--------");
		return ResultUtil.success(page);
	}
}