package com.zhongmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/user")
@Api(value = "用户服务接口")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
}
