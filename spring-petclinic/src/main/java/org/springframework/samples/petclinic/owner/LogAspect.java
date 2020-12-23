package org.springframework.samples.petclinic.owner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class LogAspect {

	Logger logger = LoggerFactory.getLogger(LogAspect.class);

	// @Around 어노테이션을 통해 JoinPoint 라는 파라미터 변수를 받을 수 있음.
	// LogExecutionTime 어노테이션이 붙어있는 메소드를 JoinPoint 라는 메소드로 파라미터 받음.
	@Around("@annotation(LogExecutionTime)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// 여기서 JoinPoint라는 변수명으로 받은 LogExecutionTime 이 붙어있는 메소드를 실행시킴.
		Object proceed = joinPoint.proceed();

		stopWatch.stop();
		logger.info(stopWatch.prettyPrint());

		return proceed;
	}

}
