package com.qibaike.tns.server.app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.qibaike.tns.server.cluster.CheckAndRemoveServiceTombstoneTask;
import com.qibaike.tns.server.cluster.PushTnsAndServiceTask;

public class ClusterScheduleServer {

	public void start() {
		Runnable task_1 = new PushTnsAndServiceTask();

		Runnable task_2 = new CheckAndRemoveServiceTombstoneTask();

		ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
		pool.scheduleWithFixedDelay(task_1, 5, 5, TimeUnit.SECONDS);

		pool.scheduleWithFixedDelay(task_2, 10, 10, TimeUnit.MINUTES);
	}
}
