package com.data.service;

import org.springframework.web.multipart.MultipartFile;

import com.data.bean.TemplateProduct;
import com.data.utils.ResultUtil;

public interface ITemplateProductService {

	/**
	 * 多条件获取模板商品信息
	 * @param param
	 * @return
	 */
	ResultUtil getTemplateProductByParam(TemplateProduct template, Integer page, Integer limit) throws Exception ;
	/**
	 * 更新
	 * @param product
	 * @return
	 */
	ResultUtil updateTemplateProduct(TemplateProduct product);
	/**
	 * 插入
	 * @param product
	 * @return
	 */
	ResultUtil insertTemplateProduct(TemplateProduct product);
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	ResultUtil deleteTemplateProduct(int id);
	/**
	 * 获取品牌菜单
	 * @param sysId
	 * @return
	 */
	ResultUtil getBrandMenu(String sysId);
	
	/**
	 * 按id查询产品信息
	 * @param id
	 * @return
	 */
	ResultUtil queryProductInfo(String id);
	
	/**
	 * 模板产品数据导入
	 * @param file
	 * @return
	 * @throws Exception
	 */
	ResultUtil uploadTemplateProductData(MultipartFile file) throws Exception;
}
