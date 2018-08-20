package com.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.config.JobConfig;

@RestController
@RequestMapping("/job")
public class JobController {
	
	private Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private JobConfig config;
	
	@RequestMapping(value = "/startJob", method = RequestMethod.POST)
	public String startJob(String expression) {
		config.setJob(expression);
		logger.info("--->>>开始定时任务<<<---");
		return "success";
	}
	
	@RequestMapping(value = "/stopJob", method = RequestMethod.GET)
	public String stopJob() {
		logger.info("--->>>停止定时任务<<<---");
		return "stop";
	}
	
	@RequestMapping(value = "/updateJob", method = RequestMethod.POST)
	public String changeJob(String expression) {
		config.setJob(expression);
		logger.info("--->>>更新定时任务<<<---");
		return "change";
	}
}
