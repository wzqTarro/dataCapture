package com.data.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

@Configuration
public class DataSourceConfiguration {

	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;
	
	@Value("${spring.datasource.initialSize}")
	private int initialSize;
	
	@Value("${spring.datasource.minIdle}")
	private int minIdle;
	
	@Value("${spring.datasource.maxActive}")
	private int maxActive;
	
	@Value("${spring.datasource.maxWait}")
	private int maxWait;
	
	@Value("${spring.datasource.testOnBorrow}")
	private boolean testOnBorrow;
	
	@Value("${spring.datasource.validationQuery}")
	private String validationQuery;
	
	@Value("${spring.datasource.poolPreparedStatements}")
	private boolean poolPreparedStatements;
	
	@Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
	private int maxPoolPreparedStatementPerConnectionSize;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource getdataSource() {
		return new DruidDataSource();
	}
	/**
	 * druid访问页配置
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public ServletRegistrationBean statViewServlet() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		servletRegistrationBean.addInitParameter("allow", "127.0.0.1");//设置ip白名单
		servletRegistrationBean.addInitParameter("deny", "");//设置ip黑名单
		//登录账号密码
		servletRegistrationBean.addInitParameter("loginUsername", "admin");
		servletRegistrationBean.addInitParameter("loginPassword", "123456");
		//是否可以重置数据
		servletRegistrationBean.addInitParameter("resetEnable", "false");
		return servletRegistrationBean;
	}
	/**
	 * 过滤规则
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean statFilter() {
		//创建过滤器
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
		//设置过滤器过滤路径
		filterRegistrationBean.addUrlPatterns("/*");
		//忽略过滤的形式
		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}
	
}
