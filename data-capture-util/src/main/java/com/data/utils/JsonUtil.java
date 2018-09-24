package com.data.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * json工具类
 * @author Alex
 *
 */
public class JsonUtil {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(Feature.ALLOW_COMMENTS, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		//取消默认timestamps格式
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		/*DeserializationConfig cfg = mapper.getDeserializationConfig();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");*/
		
	}
	
	/**
	 * bean对象转json字符串
	 * @param obj
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String toJson(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}
	
	/**
	 * json字符串转bean对象
	 * @param json
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T toBean(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, clazz);
	}
	
	/**
	 * json字符串转换成list
	 * @param json
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> List<T> toList(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		//return mapper.readValue(json, new TypeReference<List<T>>() {});
		return mapper.readValue(json, mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz));
	}
	
	/**
	 * json字符串转换成map
	 * @param json
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> Map<String, T> toMap(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, mapper.getTypeFactory().constructParametricType(HashMap.class, String.class, clazz));
	}
}
