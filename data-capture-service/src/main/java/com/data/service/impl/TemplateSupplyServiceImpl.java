package com.data.service.impl;

import java.io.IOException;
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
import com.data.constant.enums.CodeEnum;
import com.data.constant.enums.ExcelEnum;
import com.data.constant.enums.TipsEnum;
import com.data.service.ITemplateSupplyService;
import com.data.utils.CommonUtil;
import com.data.utils.DateUtil;
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
		//logger.info("----->>>>>>supply:"+ FastJsonUtil.objectToString(supply) +"<<<<<<-------");
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
	public ResultUtil querySupplyByConditiion(TemplateSupply supply, String queryDate, Integer page, Integer limit) throws Exception {
		//logger.info("----->>>>>supply:{}<<<<<------", FastJsonUtil.objectToString(supply));
		//logger.info("----->>>>>page:{}, limit:{}<<<<<------", page, limit);
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
		List<TemplateSupply> supplyList = pageRecord.getList();
		for (int i = 0; i < supplyList.size(); i++) {
			TemplateSupply templateSupply = supplyList.get(i);
			String sysId = templateSupply.getSysId();
			map.clear();
			map.put("sysId", sysId);
			map.put("queryDate", queryDate);
			int saleCount = queryCountByObject(QueryId.QUERY_COUNT_SALE_BY_PARAM, map);

			int	orderCount = queryCountByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, map);

			int rejectCount = queryCountByObject(QueryId.QUERY_COUNT_REJECT_BY_PARAM, map);

			if (saleCount == 0) {
				templateSupply.setSaleStatus(TipsEnum.DATA_IS_NULL.getValue());
			} else {				
				map.put("status", 0);
				// 是否存在异常数据
				saleCount = queryCountByObject(QueryId.QUERY_COUNT_SALE_BY_PARAM, map);
				if (saleCount == 0) {
					templateSupply.setSaleStatus(TipsEnum.GRAB_SUCCESS.getValue());
				} else {
					templateSupply.setSaleStatus(TipsEnum.DATA_EXCEPTION.getValue());
				}
			}
			
			if (orderCount == 0) {
				templateSupply.setOrderStatus(TipsEnum.DATA_IS_NULL.getValue());
			} else {				
				map.put("status", 0);
				// 是否存在异常数据
				orderCount = queryCountByObject(QueryId.QUERY_COUNT_ORDER_BY_CONDITION, map);
				if (orderCount == 0) {
					templateSupply.setOrderStatus(TipsEnum.GRAB_SUCCESS.getValue());
				} else {
					templateSupply.setOrderStatus(TipsEnum.DATA_EXCEPTION.getValue());
				}
			}
			
			if (rejectCount == 0) {
				templateSupply.setRejectStatus(TipsEnum.DATA_IS_NULL.getValue());
			} else {				
				map.put("status", 0);
				// 是否存在异常数据
				rejectCount = queryCountByObject(QueryId.QUERY_COUNT_REJECT_BY_PARAM, map);
				if (rejectCount == 0) {
					templateSupply.setRejectStatus(TipsEnum.GRAB_SUCCESS.getValue());
				} else {
					templateSupply.setRejectStatus(TipsEnum.DATA_EXCEPTION.getValue());
				}
			}
			
			map.put("queryDate", DateUtil.getCurrentDateStr());
			int stockCount = queryCountByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, map);
			if (stockCount == 0) {
				templateSupply.setStockStatus(TipsEnum.DATA_IS_NULL.getValue());
			} else {				
				map.put("status", 0);
				// 是否存在异常数据
				stockCount = queryCountByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, map);
				if (saleCount == 0) {
					templateSupply.setStockStatus(TipsEnum.GRAB_SUCCESS.getValue());
				} else {
					templateSupply.setStockStatus(TipsEnum.DATA_EXCEPTION.getValue());
				}
			}

		}
		pageRecord.setList(supplyList);
		//logger.info("---->>>>>供应链分页结果:"+ FastJsonUtil.objectToString(pageRecord) +"<<<<<<--------");
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
	public ResultUtil uploadTemplateSupplyData(MultipartFile file) {
		ExcelUtil<TemplateSupply> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> templateSupplyMapList;
		try {
			templateSupplyMapList = excelUtil.getExcelList(file, ExcelEnum.TEMPLATE_SUPPLY_TEMPLATE_TYPE.value());
			if (templateSupplyMapList == null) {
				return ResultUtil.error(CodeEnum.EXCEL_FORMAT_ERROR_DESC.value());
			}
			if(templateSupplyMapList.size() == 0) {
				return ResultUtil.error(CodeEnum.DATA_EMPTY_ERROR_DESC.value());
			}
			insert(InsertId.INSERT_BATCH_SUPPLY, templateSupplyMapList);
		} catch (IOException e) {
			return ResultUtil.error(CodeEnum.UPLOAD_ERROR_DESC.value());
		} catch (Exception se) {
			return ResultUtil.error(CodeEnum.SQL_ERROR_DESC.value());
		}
		return ResultUtil.success();
	}

	@Override
	public ResultUtil queryAliveSupplyIds() {
		List<Integer> idsList = queryListByObject(QueryId.QUERY_SUPPLY_IDS_LIST, null);
		return ResultUtil.success(idsList);
	}
}
