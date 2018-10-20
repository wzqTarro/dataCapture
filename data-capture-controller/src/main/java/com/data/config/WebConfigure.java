package com.data.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.data.interceptor.DataInterceptor;

/**
 * web请求配置类
 * @author Alex
 *
 */
@Configuration
public class WebConfigure implements WebMvcConfigurer {

	@Autowired
	private DataInterceptor dataInterceptor;
	
	@Autowired
	private DateConverter dateConverter;
	
	@Value("${spring.io.multipart.tempDir}")
	private String tempDir;
	
	/**
	 * 跨域设置
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.maxAge(3600);
	}
	
	/**
	 * 添加拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(dataInterceptor).addPathPatterns("/**")
						.excludePathPatterns("/webjars/**",
											 "/swagger-resources/**",
											 "/static/**",
											 "/js/**",
											 "/css/**",
											 "/resource/**",
											 "/html/**",
											 "/img/**");
	}
	
	/**
	 * 设置时间转换器
	 * @return
	 */
//	@SuppressWarnings("rawtypes")
//	@Bean
//	public ConversionService getConversionService() {
//		ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
//		Set<Converter> converterSet = new HashSet<>(10);
//		converterSet.add(dateConverter);
//		factory.setConverters(converterSet);
//		return factory.getObject();
//	}
	
	/**
	 * 文件上传配置
	 * @return
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		//设置文件上传io操作临时文件夹
		MultipartConfigFactory factory = new MultipartConfigFactory();
		String dir = tempDir;
		File tempFile = new File(dir);
		if(!tempFile.exists() ) {
			tempFile.mkdirs();
		}
		factory.setLocation(dir);
		return factory.createMultipartConfig();
	}
}
