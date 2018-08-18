package com.data.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAop {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 环绕通知
	 * controller方法执行前后输出日志
	 * @param point
	 * @return
	 */
	@Around("execution(* com.data.controller..*.*(..))")
	public Object doAroundAdvice(ProceedingJoinPoint point) {
		long start = System.currentTimeMillis();
		logger.info("----->>>>>调用" + point.getSignature().getDeclaringTypeName() 
				+ "方法：" + point.getSignature().getName() + " start at：" + start + "<<<<<<-----");
		try {
			Object obj = point.proceed();
			long end = System.currentTimeMillis();
			logger.info("----->>>>>调用" + point.getSignature().getDeclaringTypeName() 
					+ "方法：" + point.getSignature().getName() + " end at：" + end + ", total：" + (end - start) + "<<<<<<-----");
			return obj;
		} catch (Throwable e) {
			logger.info("----->>>>>调用" + point.getSignature().getDeclaringTypeName() 
					+ "方法：" + point.getSignature().getName() + " 失败<<<<<<-----");
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 前置通知
	 * service方法执行前日志输出
	 * @param point
	 */
	@Before("execution(* com.data.service..*.*(..))")
	public void doBeginAdvice(JoinPoint point) {
		logger.info("----->>>>>" + point.getSignature().getDeclaringTypeName() 
				+ "方法：" + point.getSignature().getName() + "<<<<<<-----");
		/**
		 * TODO
		 * 缺少输出service方法的参数名及值的通用方法
		 */
	}
}
