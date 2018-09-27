package com.data.service;

import java.util.List;

/**
 * 字典服务接口
 * @author Alex
 *
 */
public interface ICodeDictService {

	List<String> queryCodeListByServiceCode(String serviceCode);
}
