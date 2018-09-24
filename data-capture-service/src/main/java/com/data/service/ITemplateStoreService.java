package com.data.service;

import com.data.bean.TemplateStore;
import com.data.utils.ResultUtil;

public interface ITemplateStoreService {

	/**
	 * 多条件分页查询模板门店信息
	 * @param param
	 * @return
	 */
	ResultUtil getTemplateStoreByParam(TemplateStore store, Integer page, Integer limit) throws Exception ;
	/**
	 * 更新模板门店信息
	 * @param templateStore
	 * @return
	 */
	ResultUtil updateTemplateStore(TemplateStore templateStore);
	/**
	 * 插入模板门店信息
	 * @param templateStore
	 * @return
	 */
	ResultUtil insertTemplateStore(TemplateStore templateStore);
	/**
	 * 删除模板门店信息
	 * @param id
	 * @return
	 */
	ResultUtil deleteTemplateStoreById(int id);
	/**
	 * 获取系统大区及其对应省区菜单
	 * @param sysId
	 * @return
	 */
	ResultUtil getRegionMenu(String sysId);
}
