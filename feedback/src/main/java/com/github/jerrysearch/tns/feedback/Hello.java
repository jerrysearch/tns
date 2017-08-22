package com.github.jerrysearch.tns.feedback;

import com.github.jerrysearch.tns.feedback.aspectj.Feedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Hello {

    private static Logger log = LoggerFactory.getLogger(Hello.class);

    private Long time;

    public Hello(){}


    public Hello(Long time){
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public static void main(String[] args) throws Exception {

        Hello hello = new Hello(System.currentTimeMillis());

        hello.setTime(System.currentTimeMillis());
        hello.getTime();
        int i = hello.sayHello(System.currentTimeMillis(), 10);
        log.info("return is = {}", i);

        Thread t = Thread.currentThread();
        log.info(t.toString());

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        log.info("executorService = {}", executorService.toString());
    }

    /**
     * @param time 时间
     * @param tmp  变量
     * @return 变量
     */
    @Feedback
    private int sayHello(long time, int tmp) {
        log.info("time = {}, tmp = {}", time, tmp);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        log.info("#sayHello : {}", executorService.getClass().getName());
        return 0;
    }
}
