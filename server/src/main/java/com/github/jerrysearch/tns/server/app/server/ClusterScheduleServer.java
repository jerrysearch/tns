package com.github.jerrysearch.tns.server.app.server;

import com.github.jerrysearch.tns.server.app.server.AbstractServer;
import com.github.jerrysearch.tns.server.cluster.CheckAndRemoveServiceTombstoneTask;
import com.github.jerrysearch.tns.server.cluster.PushTnsAndServiceTask;
import com.github.jerrysearch.tns.server.util.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClusterScheduleServer extends AbstractServer {

    @Override
    public void start() {
        Runnable pushTnsAndServiceTask = new PushTnsAndServiceTask();
        Runnable checkAndRemoveServiceTombstoneTask = new CheckAndRemoveServiceTombstoneTask();

        ScheduledExecutorService pool = Executors
                .newSingleThreadScheduledExecutor(new NamedThreadFactory("cluster_schedule_thread",
                        true));
        pool.scheduleWithFixedDelay(pushTnsAndServiceTask, 5, 5, TimeUnit.SECONDS);
        pool.scheduleWithFixedDelay(checkAndRemoveServiceTombstoneTask, 10, 10, TimeUnit.MINUTES);
    }

    @Override
    public void shutdown() {

    }
}
