package com.github.jerrysearch.tns.client.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TaskManager {
    private static final Logger log = LoggerFactory.getLogger(TaskManager.class);
    ScheduledExecutorService pool = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("TaskManager"));

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        return proxy.taskManager;
    }

    public void submit(Runnable command, int heartbeat) {
        /**
         * 启动时间随机，避免应用内所有task的heartbeat相同，导致所有task与cluster交互过于集中
         */
        int start = ThreadLocalRandom.current().nextInt(heartbeat * 1000) / 1000;
        this.pool.scheduleWithFixedDelay(command, start, heartbeat, TimeUnit.SECONDS);
        log.debug("submit command : {} with heartbeat : {}", command.toString(), heartbeat);
    }

    private static class proxy {
        private static TaskManager taskManager = new TaskManager();
    }
}
