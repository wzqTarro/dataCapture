package com.zhongmin.util;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

/**
 * 分页类
 * @author Tarro
 * @create 2018年8月14日
 */
public class PageUtil{

	//默认页码
	public static final String PAGE_NUM = "1";
	
	//默认内容数
	public static final String PAGE_SIZE = "10";
	
	//页码
	public static final String PAGE = "pageNum";
	
	//内容数
	public static final String RP = "pageSize";

	public static Map<String, Integer> getPageNumAndPageSize(String pageNum, String pageSize) {
		Map<String, Integer> page = Maps.newHashMap();
		
		if("0".equals(pageNum) || StringUtils.isNoneBlank(pageNum)) {
			pageNum = PAGE_NUM;
		}
		if("0".equals(pageSize) || StringUtils.isNoneBlank(pageSize)) {
			pageSize = PAGE_SIZE;
		}
		page.put("pageNum", (Integer.valueOf(pageNum) - 1) * Integer.valueOf(pageSize));
		page.put("pageSize", Integer.valueOf(pageSize));
		return page;
	}
	
}
