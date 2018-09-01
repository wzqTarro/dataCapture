package com.data.utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	//public static InputStream in;
	
	static {
		loadProperties();
	}
	
	public static void loadProperties() {
		InputStream in = null;
		prop = new Properties();
		try {
			in = PropertiesUtil.class.getClassLoader().getResourceAsStream("exception.properties");
			//BufferedInputStream buf = new BufferedInputStream(in);
			prop.load(in);
		} catch (FileNotFoundException e) {
			logger.info("--->>>exception.properties文件未找到<<<---");
			e.printStackTrace();
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
