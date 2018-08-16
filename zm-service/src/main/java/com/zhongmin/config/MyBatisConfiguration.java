package com.zhongmin.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableTransactionManagement
public class MyBatisConfiguration implements TransactionManagementConfigurer {
	
	@Value("${mybatis.mapper-locations}")
	private String mapperLocation;
	
	@Autowired
	private DataSource dataSource;

	@Bean
	public SqlSessionFactoryBean getSqlSessionFactoryBean(DataSource dataSource) throws IOException {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		factory.setMapperLocations(resolver.getResources(mapperLocation));
		return factory;
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {
		return new SqlSessionTemplate(factory);
	}

	@Bean
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}

}
