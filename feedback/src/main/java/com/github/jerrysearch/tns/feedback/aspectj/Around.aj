package com.github.jerrysearch.tns.feedback.aspectj;

import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public aspect Around {

    private final Logger log = LoggerFactory.getLogger(Around.class);

    /*
     * 切入点 pointcut
     */
    // pointcut greeting() : execution(public int sayHello(long, int));

    public pointcut atExecution(): execution(@Feedback * *(..));

    public pointcut aroundExecutors(): call(public * java.util.concurrent.Executors.new*(..));

    public pointcut withinCall(): withincode(@Feedback * *(..));

    /*
     * 通知 advice
     */
    ExecutorService around(): aroundExecutors() && withinCall() {

        log.info("Around begin !");
        Thread t = Thread.currentThread();
        log.info("CurrentThread --> {}", t.getName());
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

        log.info("thisJoinPointStaticPart.getSignature() = {}", thisJoinPointStaticPart.getSignature());
        long begin = System.currentTimeMillis();
        ExecutorService executorService = proceed();

        if(executorService instanceof ThreadPoolExecutor){
            ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutor.class.cast(executorService);
            log.info("===============================");
            log.info("Queue is : {}", threadPoolExecutor.getQueue().getClass().getSimpleName());
            log.info("TaskCount is : {}", threadPoolExecutor.getTaskCount());
            log.info("===============================");
        }else{
            log.info("the executorService is not instanceof ThreadPoolExecutor and it type is : {}", executorService.getClass().getName());
        }
        long end = System.currentTimeMillis();
        log.info("Spend --> {} ms", (end - begin));
        log.info("Around end !");
        log.info("just test end");
        return executorService;
    }
}
