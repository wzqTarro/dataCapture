package com.data.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 时间类型转换器
 * @author Alex
 *
 */
@Component
public class DateConverter implements Converter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
	
	@Override
	public Date convert(String source) {
		SimpleDateFormat sdf = null;
		try {
			sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return sdf.parse(source);
		} catch (ParseException e) {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return sdf.parse(source);
			} catch (ParseException e1) {
				logger.error("--->>>yyyy-MM-dd类型时间转换异常<<<--");
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return null;
	}

}
