package com.data.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.poi.ss.usermodel.DataFormat;

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
	
	public static String createWorkNo() {
		StringBuilder builder = new StringBuilder();
		builder.append("by");
		SimpleDateFormat sdf = new SimpleDateFormat("yyss");
		builder.append(sdf.format(new Date()));
		Random random = new Random();
		builder.append(random.nextInt(1000));
		return builder.toString();
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
		if(obj == null) {
			return false;
		}
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
		if(obj == null) {
			return false;
		}
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
	/**
	 * 浮点数为空返回0
	 * @param d
	 * @return
	 */
	public static double toDoubleOrZero(Double d) {
		return (null == d) ? 0 : d;
	}
	/**
	 * 整数为空返回0
	 * @param i
	 * @return
	 */
	public static int toIntOrZero(Integer i) {
		return (null == i) ? 0 :i;
	}
	/**
	 * 保留两位小数
	 * @param condition
	 * @param d
	 * @return
	 */
	public static double setScale(String condition, double d) {
		DecimalFormat dataFormat = new DecimalFormat(condition);
		dataFormat.setRoundingMode(RoundingMode.HALF_UP);
		return Double.valueOf(dataFormat.format(d));
	}
}
