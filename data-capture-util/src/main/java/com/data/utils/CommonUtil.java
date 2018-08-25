package com.data.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 公共工具类
 * @author alex
 *
 */
public class CommonUtil {
	
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
	
	/**
	 * 判断元素是否在数组里
	 * @param obj
	 * @param array
	 * @return
	 */
	public static boolean isInArray(Object obj, Object[] array) {
		if(array == null || array.length == 0) {
			return false;
		}
		for(int i = 0; i < array.length; i++) {
			if(obj.equals(array[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断元素是否在集合列表里
	 * @param obj
	 * @param list
	 * @return
	 */
	public static boolean isInList(Object obj, List<?> list) {
		if(list == null || list.size() == 0) {
			return false;
		}
		for(int i = 0; i < list.size(); i++) {
			if(obj.equals(list.get(i))) {
				return true;
			}
		}
		return false;
	}
}
