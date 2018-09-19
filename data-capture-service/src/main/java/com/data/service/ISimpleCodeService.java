package com.data.service;

import com.data.bean.SimpleCode;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface ISimpleCodeService {

	/**
	 * 多条件分页查询
	 * @param param
	 * @return
	 */
	ResultUtil getSimpleCodeByParam(SimpleCode simpleCode, Integer page, Integer limit)throws Exception ;
	/**
	 * 更新
	 * @param simpleCode
	 * @return
	 */
	ResultUtil updateSimpleCode(SimpleCode simpleCode);
	/**
	 * 插入
	 * @param simpleCode
	 * @return
	 */
	ResultUtil insertSimpleCode(SimpleCode simpleCode);
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	ResultUtil deleteSimpleCode(int id);
}
