package com.zhongmin.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;

/**
 * fastJson工具类
 * @author Tarro
 * @create 2018年8月14日
 */
public class FastJsonUtil {
	
	/**
	 * 对象转json字符串
	 * @param obj
	 * @return
	 */
	public static String objectToString(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * json字符串转java对象
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T jsonToObject(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}
	/**
	 * json字符串转java数组
	 * @param json
	 * @return
	 */
	public static Object[] jsonToObjectArray(String json) {
		JSONArray array = JSONArray.parseArray(json);
		return array.toArray();
	}
	/**
	 * json对象集合表达式得到java对象列表
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static List<?> jsonToList(String json, Class<?> clazz) {
		return JSONArray.parseArray(json, clazz);
	}
	/**
	 * map转对象
	 * @param map
	 * @param clazz
	 * @return
	 */
 	public static Object mapToObject(Map<String, String> map, Class<?> clazz) {
 		try{
	 		if (map == null) {
	 			return null;
	 		}
	 		Object obj = clazz.newInstance();
	 		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
	 		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
	 		for (PropertyDescriptor property : propertyDescriptors) {
	 			Method setter = property.getWriteMethod();
	 			if (setter != null) {
	 				setter.invoke(obj, map.get(property.getName()));
	 			}
	 		}
	 		return obj;
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return null;
 	}
 	
 	/**
 	 * 
 	 * @param obj
 	 * @return
 	 * @throws Exception
 	 */
 	public static Map<String, Object> objectToMap(Object obj) throws Exception {    
        if(obj == null)  
            return null;      
  
        Map<String, Object> map = Maps.newHashMap();   
  
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {    
            String key = property.getName();    
            if (key.compareToIgnoreCase("class") == 0) {   
                continue;  
            }  
            Method getter = property.getReadMethod();  
            Object value = getter!=null ? getter.invoke(obj) : null;  
            map.put(key, value);  
        }    
  
        return map;  
    }    
}
