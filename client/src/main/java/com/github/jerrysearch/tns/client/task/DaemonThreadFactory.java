package com.github.jerrysearch.tns.client.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {

    private final String name;
    private final AtomicInteger index = new AtomicInteger();

    protected DaemonThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(this.name + "_" + this.index.getAndIncrement());
        t.setDaemon(true);
        return t;
    }

}
