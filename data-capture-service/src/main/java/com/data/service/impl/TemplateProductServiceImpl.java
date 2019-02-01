package com.data.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.data.bean.TemplateProduct;
import com.data.constant.PageRecord;
import com.data.constant.RedisAPI;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.ExcelEnum;
import com.data.constant.enums.TipsEnum;
import com.data.service.IRedisService;
import com.data.service.ITemplateProductService;
import com.data.utils.CommonUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.RedisUtil;
import com.data.utils.ResultUtil;

@Service
public class TemplateProductServiceImpl extends CommonServiceImpl implements ITemplateProductService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IRedisService redisService;
	
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

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil uploadTemplateProductData(MultipartFile file) throws Exception {
		ExcelUtil<TemplateProduct> excelUtil = new ExcelUtil<>();
//		String[] headers = new String[]{"商品编号", "系统编号", "系统",	"单品编码", "条码",	"存货编码",
//				"单品名称", "标准名称", "品牌", "分类",	"系列", "功能", "材质",	"包装数量", "箱装规格", "货号",	
//				"零售价格", "不含税供价",	"含税供价"};
		
		List<Map<String, Object>> productMapList = excelUtil.getExcelList(file, ExcelEnum.TEMPLATE_PRODUCT_TEMPLATE_TYPE.value());
		if (productMapList == null) {
			return ResultUtil.error("格式不符，导入失败");
		}
		String productStr = JSON.toJSONString(productMapList);
		List<TemplateProduct> productList = JSON.parseArray(productStr, TemplateProduct.class);
		
		// 原有模板商品
		List<TemplateProduct> templateProductList = redisService.queryTemplateProductList();
		
		Iterator<TemplateProduct> it = productList.iterator();
		while (it.hasNext()) {
			TemplateProduct product = it.next();
			
			// 条码
			String simpleBarCode = (String) JSONPath.eval(product, "$.simpleBarCode");
			
			// 系统编号
			String sysId = (String) JSONPath.eval(product, "$.sysId");
			
			if (StringUtils.isBlank(simpleBarCode)) {
				return ResultUtil.error("条码不能为空");
			} 
			if (StringUtils.isBlank(sysId)) {
				return ResultUtil.error("系统编号不能为空");
			}
			
			// 查询原有数据中是否存在
			List<TemplateProduct> tempList = (List<TemplateProduct>) JSONPath.eval(templateProductList, "$[sysId = '"+ sysId +"']"); 
			tempList = (List<TemplateProduct>) JSONPath.eval(tempList, "$[simpleBarCode = '"+ simpleBarCode +"']"); 
			
			if (!CollectionUtils.isEmpty(tempList)) {
				Integer id = tempList.get(0).getId();
				product.setId(id);
				update(UpdateId.UPDATE_PRODUCT_BY_MESSAGE, product);
				it.remove();
			}
		}
		insert(InsertId.INSERT_BATCH_PRODUCT_LIST, productList);
		RedisUtil.del(RedisAPI.PRODUCT_TEMPLATE);
		redisService.queryTemplateProductList();
		return ResultUtil.success();
	}
	
	public static void main(String[] args) {
		List<TemplateProduct> list = new ArrayList<>();
		TemplateProduct p1 = new TemplateProduct();
		p1.setBoxStandard("1");
		System.err.println((String) JSONPath.eval(p1, "$.boxStandard"));
	}
}
