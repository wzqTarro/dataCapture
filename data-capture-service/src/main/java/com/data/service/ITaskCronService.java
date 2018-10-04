package com.data.service;

import com.data.bean.TaskCron;
import com.data.utils.ResultUtil;

/**
 * 定时任务表达式服务接口
 * @author Alex
 *
 */
public interface ITaskCronService {

	/**
	 * 组装定时任务表达式
	 * 指定前端传值未选中的为空值的返回'*' 年份未选返回'?'
	 * @param cron
	 * @return
	 */
	ResultUtil buildTaskCronExpression(TaskCron cron);
}
