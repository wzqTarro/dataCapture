package com.data.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公共工具类
 * @author alex
 *
 */
public class CommonUtil {

	private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	/**
	 * 判断一个对象是否为空
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isBlank(Object obj) {
		if(obj == null) {
			return true;
		}
		if(obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		}
		if(obj instanceof String) {
			return ((String) obj) == null || "".equals(((String) obj).trim());
		}
		if(obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		}
		if(obj instanceof List) {
			return ((List) obj).isEmpty();
		}
		if(obj instanceof Map) {
			return ((Map) obj).isEmpty();
		}
		if(obj instanceof Object[]) {
			Object[] object = (Object[]) obj;
			if(object.length == 0) {
				return true;
			}
			boolean flag = true;
			for(int i = 0; i < object.length; i++) {
				if(!isBlank(object[i])) {
					flag = false;
					break;
				}
			}
			return flag;
		}
		return false; 
	}
	
	/**
	 * 判断一个对象是否非空
	 * @param obj
	 * @return
	 */
	public static boolean isNotBlank(Object obj) {
		return !isBlank(obj);
	}
	
	/**
	 * 按照分隔符将字符串转换成数组
	 * @param ids
	 * @param separator
	 * @return
	 */
	public static String[] parseIdsCollection(String ids, String separator) {
		if(ids == null) {
			return null;
		}
 		String[] arr = ids.split(separator);
		if(arr != null && arr.length != 0) {
			return arr;
		}
		return null;
	}
	
	/**
	 * 生成UUID
	 * @return
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static void main(String[] args) {
		String[] arr = parseIdsCollection("451,452,453,454", ",");
		for(int i = 0; i < arr.length; i++) {
			System.err.println(arr[i]);			
		}
	}
}
