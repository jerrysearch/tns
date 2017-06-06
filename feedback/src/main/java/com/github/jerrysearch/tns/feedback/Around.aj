package com.github.jerrysearch.tns.feedback;

import java.util.Arrays;

import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public aspect Around percflow(feedback()) {

	private final Logger log = LoggerFactory.getLogger(Around.class);

	/**
	 * 切入点 pointcut
	 */
	// pointcut greeting() : execution(public int sayHello(long, int));

	public pointcut feedback() : @annotation(Feedback);

	public pointcut atExecution() : execution(* *(..));

	/**
	 * 通知 advice
	 */
	int around() : feedback() && atExecution() {

		log.info("Around begin !");
		Thread t = Thread.currentThread();
		log.info("CurentThread --> {}", t.getName());
		Object[] args = thisJoinPoint.getArgs();
		log.info("args = {}", Arrays.toString(args));
		String className = thisJoinPoint.getClass().getName();
		log.info("className = {}", className);

		log.info("thisJoinPoint = {}", thisJoinPoint);
		log.info("thisJoinPoint.getStaticPart = {}", thisJoinPoint.getStaticPart());

		log.info("thisJoinPoint.getKind() = {}", thisJoinPoint.getKind());

		log.info("thisJoinPoint.getSignature() = {}", thisJoinPoint.getSignature());
		log.info("thisJoinPoint.getSignature().getName() = {}", thisJoinPoint.getSignature().getName());
		log.info("thisJoinPoint.getSignature().getDeclaringTypeName() = {}",
				thisJoinPoint.getSignature().getDeclaringTypeName());
		log.info("thisJoinPoint.getArgs() = {}", Arrays.toString(thisJoinPoint.getArgs()));
		log.info("thisJoinPoint.getTarget() = {}", thisJoinPoint.getTarget());
		CodeSignature codeSignature = (CodeSignature) thisJoinPointStaticPart.getSignature();
		log.info("codeSignature.getParameterNames() = {}", Arrays.toString(codeSignature.getParameterNames()));
		log.info("codeSignature.getParameterTypes() = {}", Arrays.toString(codeSignature.getParameterTypes()));
		log.info("codeSignature.getName() = {}", codeSignature.getName());
		log.info("codeSignature = {}", codeSignature.toLongString());
		log.info("thisJoinPointStaticPart.getSourceLocation() = {}", thisJoinPointStaticPart.getSourceLocation());

		log.info("thisJoinPointStaticPart.getSourceLocation() = {}", thisJoinPointStaticPart.getSignature());
		long begin = System.currentTimeMillis();
		int i = proceed() + 1;
		long end = System.currentTimeMillis();
		log.info("Spend --> {}", (end - begin));
		log.info("Around end !");

		return i;
	}
}
