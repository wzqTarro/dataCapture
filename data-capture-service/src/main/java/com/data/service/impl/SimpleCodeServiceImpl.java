package com.data.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.SimpleCode;
import com.data.bean.TemplateProduct;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.TipsEnum;
import com.data.service.ISimpleCodeService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

@Service
public class SimpleCodeServiceImpl extends CommonServiceImpl implements ISimpleCodeService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ResultUtil getSimpleCodeByParam(SimpleCode simpleCode, Integer page, Integer limit) throws Exception {
		logger.info("---->>>>>前端参数simpleCode：{}, page:{}, limit:{}<<<<<-------", 
				FastJsonUtil.objectToString(simpleCode), page, limit);
		Map<String, Object> map = null;
		if (null != simpleCode) {
			map = new HashMap<>();
			
			// 单品名称
			if (CommonUtil.isNotBlank(simpleCode.getSimpleName())) {
				map.put("simpleName", simpleCode.getSimpleName());
			}
			
			// 条码
			if (CommonUtil.isNotBlank(simpleCode.getBarCode())) {
				map.put("barCode", simpleCode.getBarCode());
			}
		}
		logger.info("------>>>>>标准条码查询条件:{}<<<<<------", FastJsonUtil.objectToString(map));
		PageRecord<TemplateProduct> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_SIMPLE_CODE_BY_PARAM, QueryId.QUERY_SIMPLE_CODE_BY_PARAM, 
				map, page, limit);
		logger.info("------>>>>>模板商品分页信息:{}<<<<<------", FastJsonUtil.objectToString(pageRecord));
		return ResultUtil.success(pageRecord);
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil updateSimpleCode(SimpleCode simpleCode) {
		if (null == simpleCode) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		if (null == simpleCode.getId()) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		update(UpdateId.UPDATE_SIMPLE_CODE_BY_MESSAGE, simpleCode);
		return ResultUtil.success();
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil insertSimpleCode(SimpleCode simpleCode) {
		if (null == simpleCode) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		simpleCode.setId(null);
		insert(InsertId.INSERT_SIMPLE_CODE_BY_MESSAGE, simpleCode);
		return ResultUtil.success();
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil deleteSimpleCode(int id) {
		if (id == 0) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		delete(DeleteId.DELETE_SIMPLE_CODE_BY_ID, id);
		return ResultUtil.success();
	}
}
