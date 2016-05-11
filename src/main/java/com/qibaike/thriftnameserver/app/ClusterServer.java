package com.qibaike.thriftnameserver.app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.qibaike.thriftnameserver.cluster.CheckAndRemoveServiceTombstoneTask;
import com.qibaike.thriftnameserver.cluster.PushTnsAndServiceTask;

public class ClusterServer {

	public void start() {
		Runnable task_1 = new PushTnsAndServiceTask();

		Runnable task_2 = new CheckAndRemoveServiceTombstoneTask();

		ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
		pool.scheduleWithFixedDelay(task_1, 5, 5, TimeUnit.SECONDS);

		pool.scheduleWithFixedDelay(task_2, 10, 10, TimeUnit.MINUTES);
	}
}
