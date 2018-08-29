package com.data.service;

import com.data.bean.TemplateProduct;
import com.data.utils.ResultUtil;

public interface ITemplateProductService {

	/**
	 * 多条件获取模板商品信息
	 * @param param
	 * @return
	 */
	ResultUtil getTemplateProductByParam(String param) throws Exception ;
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
	ResultUtil deleteTemplateProduct(Integer id);
}
