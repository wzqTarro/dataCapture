package com.data.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.TemplateProduct;
import com.data.constant.PageRecord;
import com.data.constant.RedisAPI;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.TipsEnum;
import com.data.service.ITemplateProductService;
import com.data.utils.CommonUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.RedisUtil;
import com.data.utils.ResultUtil;

@Service
public class TemplateProductServiceImpl extends CommonServiceImpl implements ITemplateProductService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public ResultUtil getTemplateProductByParam(TemplateProduct product, Integer page, Integer limit) throws Exception {
		
		logger.info("------>>>>>product:{}<<<<<------", FastJsonUtil.objectToString(product));
		logger.info("------>>>>>page:{},limit:{}<<<<<------", page, limit);
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
		logger.info("------>>>>>模板商品查询条件:{}<<<<<------", FastJsonUtil.objectToString(map));
		PageRecord<TemplateProduct> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_PRODUCT_BY_PARAM, QueryId.QUERY_PRODUCT_BY_PARAM, 
				map, page, limit);
		logger.info("------>>>>>模板商品分页信息:{}<<<<<------", FastJsonUtil.objectToString(pageRecord));
		return ResultUtil.success(pageRecord);
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
		RedisUtil.del(RedisAPI.PRODUCT_TEMPLATE);
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
		RedisUtil.del(RedisAPI.PRODUCT_TEMPLATE);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil deleteTemplateProduct(int id) {
		if (0 == id) {
			return ResultUtil.error(TipsEnum.ID_ERROR.getValue());
		}
		logger.info("------>>>>>>>id:{}<<<<<<<--------", id);
		delete(DeleteId.DELETE_PRODUCT_BY_ID, id);
		RedisUtil.del(RedisAPI.PRODUCT_TEMPLATE);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil getBrandMenu(String sysId) {
		List<String> brandList = queryListByObject(QueryId.QUERY_PRODUCT_BRAND, sysId);
		return ResultUtil.success(brandList);
	}

	@Override
	public ResultUtil queryProductInfo(String id) {
		if(CommonUtil.isNotBlank(id)) {
			Map<String, Object> params = new HashMap<>(4);
			params.put("id", Integer.parseInt(id));
			TemplateProduct product = (TemplateProduct) queryObjectByParameter(QueryId.QUERY_PRODUCT_BY_ID, params);
			return ResultUtil.success(product);
		}
		return null;
	}

	@Transactional(rollbackFor = {Exception.class})
	@Override
	public ResultUtil importProductExcel(MultipartFile file) throws IOException {
		ExcelUtil<TemplateProduct> excelUtil = new ExcelUtil();
		List<Map<String, Object>> productMapList = excelUtil.getExcelList(file);
		if (productMapList == null) {
			return ResultUtil.error("导入excel表不符合规范或者内容为空");
		}
		Map<String, Object> map = null;
		TemplateProduct product = null;
		List<TemplateProduct> productList = new ArrayList<>(productMapList.size());
		for (int i = 0, size = productMapList.size(); i < size; i++) {
			map = productMapList.get(i);
			product = new TemplateProduct();
			product.setProductId((String)map.get("商品编号"));
			product.setSysId(String.valueOf(map.get("系统编号")));
			product.setSysName((String)map.get("系统"));
			product.setSimpleCode(String.valueOf((double)map.get("单品编码")));
			product.setSimpleBarCode(String.valueOf((double)map.get("条码")));
			product.setBoxStandard((String)map.get("箱规"));
			product.setBrand((String)map.get("品牌"));
			product.setClassify((String)map.get("分类"));
			product.setExcludeTaxPrice(new BigDecimal((double)map.get("含税供价")));
			product.setIncludeTaxPrice(new BigDecimal((double)map.get("不含税供价")));
			product.setFunc((String)map.get("功能"));
			product.setMaterial((String)map.get("材质"));
			product.setPiecesNum((int)map.get("包装数量"));
			product.setSellPrice(new BigDecimal((double)map.get("零售价格")));
			product.setSeries((String)map.get("系列"));
			product.setStockCode(String.valueOf((double)map.get("存货编码")));
			product.setStandardName((String)map.get("标准名称"));
			product.setSimpleName((String)map.get("单品名称"));
			product.setStockNo((String)map.get("货号"));
			productList.add(product);
		}
		logger.info(FastJsonUtil.objectToString(productList));
		//insert(InsertId.INSERT_BATCH_PRODUCT, productList);
		return ResultUtil.success();
	}
}
