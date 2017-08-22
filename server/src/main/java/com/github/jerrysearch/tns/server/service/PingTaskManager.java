package com.github.jerrysearch.tns.server.service;

import com.github.jerrysearch.tns.protocol.rpc.TSNode;
import com.github.jerrysearch.tns.server.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PingTaskManager {
    /**
     * 全局ping任务调度线程
     */
    private final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(2,
            new NamedThreadFactory("ping_task_manager", true));
    private final Logger log = LoggerFactory.getLogger(PingTaskManager.class);

    private PingTaskManager() {
        this.pool.setRemoveOnCancelPolicy(true);
    }

    public static PingTaskManager getInstance() {
        return proxy.instance;
    }

    public void submit(TSNode tsnode) {
        PingTask task = new PingTask(tsnode);
        int pingFrequency = tsnode.getPingFrequency();
        /**
         * 启动时间随机，避免同时增加大量任务，导致任务执行过于集中
         */
        int startTime = ThreadLocalRandom.current().nextInt(pingFrequency * 1000) / 1000;
        ScheduledFuture<?> future = this.pool.scheduleWithFixedDelay(task, startTime,
                pingFrequency, TimeUnit.SECONDS);
        task.setFuture(future);
        this.log.info("submit {} with initialDelay [{}], long delay [{}] ok!", task, startTime,
                pingFrequency);
    }

    private static class proxy {
        private static PingTaskManager instance = new PingTaskManager();
    }
}
