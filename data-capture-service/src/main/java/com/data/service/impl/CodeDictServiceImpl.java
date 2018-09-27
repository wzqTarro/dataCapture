package com.data.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.data.constant.dbSql.QueryId;
import com.data.service.ICodeDictService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;

/**
 * 字典服务实现类
 * @author Alex
 *
 */
@Service
public class CodeDictServiceImpl extends CommonServiceImpl implements ICodeDictService {
	
	private static final Logger logger = LoggerFactory.getLogger(CodeDictServiceImpl.class);

	@Override
	public List<String> queryCodeListByServiceCode(String serviceCode) {
		
		if(CommonUtil.isBlank(serviceCode)) {
			return new ArrayList<String>(10);
		}
		logger.info("--->>>字典查询服务编码: {}<<<---", serviceCode);
		List<String> codeDictList = queryListByObject(QueryId.QUERY_CODE_DICT_LIST_BY_SERVICE_CODE, serviceCode);
		logger.info("--->>>服务查询集合: {}<<<---", FastJsonUtil.objectToString(codeDictList));
		if(CommonUtil.isNotBlank(codeDictList)) {
			return codeDictList;
		}
		return null;
	}

}
