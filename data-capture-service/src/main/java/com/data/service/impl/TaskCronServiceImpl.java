//package com.data.service.impl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.data.bean.TaskCron;
//import com.data.service.ITaskCronService;
//import com.data.utils.CommonUtil;
//import com.data.utils.ResultUtil;
//
//@Service
//public class TaskCronServiceImpl extends CommonServiceImpl implements ITaskCronService {
//	
//	private static final Logger logger = LoggerFactory.getLogger(TaskCronServiceImpl.class);
//	
//	/**任意通配符**/
//	private static final String TASK_CRON_PATTERN_ANY = "*";
//	/**增量通配符**/
//	private static final String TASK_CRON_PATTERN_INCREMENT = "/";
//	/**日期和星期通配符**/
//	private static final String TASK_CRON_PATTERN_DATE_OR_WEEKEND = "?";
//
//	@Override
//	public ResultUtil buildTaskCronExpression(TaskCron cron) {
//		if(CommonUtil.isBlank(cron.getSeconds())) {
//			return ResultUtil.error("定时任务秒数不能为空");
//		}
//		if(CommonUtil.isBlank(cron.getMinutes())) {
//			return ResultUtil.error("定时任务分钟不能为空");
//		}
//		if(CommonUtil.isBlank(cron.getHours())) {
//			return ResultUtil.error("定时任务小时不能为空");
//		}
//		if(CommonUtil.isBlank(cron.getMonths())) {
//			return ResultUtil.error("定时任务月份不能为空");
//		}
//		if(CommonUtil.isBlank(cron.getDates())) {
//			return ResultUtil.error("定时任务日期不能为空");
//		}
//		if(CommonUtil.isBlank(cron.getWeekends())) {
//			return ResultUtil.error("定时任务星期不能为空");
//		}
//		if(CommonUtil.isBlank(cron.getYears())) {
//			return ResultUtil.error("定时任务年份不能为空");
//		}
//		StringBuilder builder = new StringBuilder();
//		String seconds = cron.getSeconds();
//		if(TASK_CRON_PATTERN_ANY.equals(seconds)) {
//			builder.append("0 ");
//		} else {
//			builder.append(cron.getSeconds() + " ");
//		}
//		String[] minutes = cron.getMinutes();
//		if(minutes.length == 1) {
//			if()
//		}
//		for(int i = 0; i < minutes.length; i++) {
//			if(TASK_CRON_PATTERN_ANY.equals(minutes[i])) {
//				builder.append("");
//			} else {
//				builder.append();
//			}
//		}
//		return null;
//	}
//	
//	/**
//	 * 组装cron表达式
//	 * @param pattern
//	 * @param times
//	 * @return
//	 */
//	private StringBuilder matchCronExpression(String pattern, String times) {
//		if(pattern.equals(times)) {
//			if(TASK_CRON_PATTERN_ANY.equals(pattern)) {
//				
//			}
//		}
//	}
//	
//
//}
