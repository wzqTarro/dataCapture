package com.data.config;

import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 定时任务配置类
 * @author alex
 *
 */
@Component
public class JobConfig {

	private Logger logger = LoggerFactory.getLogger(JobConfig.class);
	
	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		return new ThreadPoolTaskScheduler();
	}
	
	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	public ScheduledFuture<?> future;
	
	private String express = "";
	
	public void setJob(String express) {
		stopJob();
		future = threadPoolTaskScheduler.schedule(() -> {
			//业务代码
			logger.info("--->>>定时任务开启<<<---");
		}, (context) -> {
			logger.info("--->>>定时任务更改***新的cron表达式=" + express + "<<<---");
			CronTrigger cronTrigger = new CronTrigger(express);
			return cronTrigger.nextExecutionTime(context);
		});
	}
	
	public String getJob() {
		return express;
	}
	
	public void stopJob() {
		if(future != null) {
			future.cancel(true);
		}
	}
}
