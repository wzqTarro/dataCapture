package com.data.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置文件读取类
 * @author Alex
 *
 */
public class PropertiesUtil {
	
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	public static Properties prop;
	
	public static InputStream in;
	
	static {
		loadProperties();
	}
	
	public static void loadProperties() {
		in = PropertiesUtil.class.getResourceAsStream("/exception.properties");
		try {
			BufferedInputStream buf = new BufferedInputStream(in);
			prop.load(buf);
		} catch (IOException e) {
			logger.info("--->>>读取properties文件异常<<<---");
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getMessage(String key) {
		return prop.getProperty(key);
	}
}
