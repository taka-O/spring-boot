package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
	@Before("within(com.example.demo.controller.EmployeeController)")
	public void startLog(JoinPoint jp) {
		log.info("{}: Before処理", jp.getSignature());
	}

	@After("execution(* com.example.demo.controller.*Controller.*(..))")
	public void EndLog(JoinPoint jp) {
		log.info("{}: After処理", jp.getSignature());
	}
}
