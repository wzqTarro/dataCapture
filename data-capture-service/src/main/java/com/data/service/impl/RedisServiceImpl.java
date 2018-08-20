package com.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.service.RedisService;
import com.data.utils.RedisUtil;

/**
 * 缓存实现类
 * 在其他实现类注入
 * @author Administrator
 *
 */
@Service
public class RedisServiceImpl extends CommonServiceImpl implements RedisService {

	@Autowired
	private RedisUtil redisUtil;
	
	
	
}
