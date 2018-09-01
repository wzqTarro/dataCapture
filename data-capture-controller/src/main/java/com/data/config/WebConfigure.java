package com.data.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.data.interceptor.DataInterceptor;

/**
 * 视图请求配置类
 * @author Alex
 *
 */
@Configuration
public class WebConfigure implements WebMvcConfigurer {

	@Autowired
	private DataInterceptor dataInterceptor;
	
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
}
