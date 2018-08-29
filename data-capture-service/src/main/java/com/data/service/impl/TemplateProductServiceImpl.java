package com.data.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.data.bean.TemplateProduct;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.dto.CommonDTO;
import com.data.service.ITemplateProductService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.google.common.collect.Maps;

@Service
public class TemplateProductServiceImpl extends CommonServiceImpl implements ITemplateProductService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public ResultUtil getTemplateProductByParam(String param) throws Exception {
		CommonDTO common = FastJsonUtil.jsonToObject(param, CommonDTO.class);
		TemplateProduct product = FastJsonUtil.jsonToObject(param, TemplateProduct.class);
		logger.info("------>>>>>common:{}<<<<<------", FastJsonUtil.objectToString(common));
		logger.info("------>>>>>product:{}<<<<<------", FastJsonUtil.objectToString(product));
		Map<String, Object> map = new HashMap<>(10);
		if (null != product) {
			// 商品编号
			if (CommonUtil.isNotBlank(product.getProductId())) {
				map.put("productId", product.getProductId());
			}
			
			// 单品名称
			if (CommonUtil.isNotBlank(product.getSimpleName())) {
				map.put("simpleName", product.getSimpleName());
			}
			
			// 标准名称
			if (CommonUtil.isNotBlank(product.getStandardName())) {
				map.put("standardName", product.getStandardName());
			}
			
			// 单品条码
			if (CommonUtil.isNotBlank(product.getSimpleBarCode())) {
				map.put("simpleBarCode", product.getSimpleBarCode());
			}
			
			// 单品编码
			if (CommonUtil.isNotBlank(product.getSimpleCode())) {
				map.put("simpleCode", product.getSimpleCode());
			}
			
			// 存货编码
			if (CommonUtil.isNotBlank(product.getStockCode())) {
				map.put("stockCode", product.getStockCode());
			}
			
			// 货号
			if (CommonUtil.isNotBlank(product.getStockNo())) {
				map.put("stockNo", product.getStockNo());
			}
			
			// 品牌
			if (CommonUtil.isNotBlank(product.getBrand())) {
				map.put("brand", product.getBrand());
			}
			
			// 分类
			if (CommonUtil.isNotBlank(product.getClassify())) {
				map.put("classify", product.getClassify());
			}
			
			// 系列
			if (CommonUtil.isNotBlank(product.getSeries())) {
				map.put("series", product.getSeries());
			}
		}
		logger.info("------>>>>>map:{}<<<<<------", FastJsonUtil.objectToString(map));
		PageRecord<TemplateProduct> page = queryPageByObject(QueryId.QUERY_COUNT_PRODUCT_BY_PARAM, QueryId.QUERY_PRODUCT_BY_PARAM, 
				map, common.getPage(), common.getLimit());
		logger.info("------>>>>>page:{}<<<<<------", FastJsonUtil.objectToString(page));
		return ResultUtil.success(page);
	}

	@Override
	public ResultUtil updateTemplateProduct(TemplateProduct product) {
		if (null == product) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		if (CommonUtil.isBlank(product.getId())) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		logger.info("------>>>>>>>product:{}<<<<<<<--------", FastJsonUtil.objectToString(product));
		update(UpdateId.UPDATE_PRODUCT_BY_MESSAGE, product);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil insertTemplateProduct(TemplateProduct product) {
		if (null == product) {
			return ResultUtil.error(TipsEnum.OPERATE_DATA_ERROR.getValue());
		}
		product.setId(null);
		logger.info("------>>>>>>>product:{}<<<<<<<--------", FastJsonUtil.objectToString(product));
		insert(InsertId.INSERT_PRODUCT_BY_MESSAGE, product);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil deleteTemplateProduct(Integer id) {
		if (null == id || 0 == id) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		logger.info("------>>>>>>>id:{}<<<<<<<--------", id);
		delete(DeleteId.DELETE_PRODUCT_BY_ID, id);
		return ResultUtil.success();
	}

	
}
