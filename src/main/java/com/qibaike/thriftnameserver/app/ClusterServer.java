package com.qibaike.thriftnameserver.app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.qibaike.thriftnameserver.cluster.CheckAndRemoveServiceTombstoneTask;
import com.qibaike.thriftnameserver.cluster.PushServiceTask;
import com.qibaike.thriftnameserver.cluster.PushTnsTask;

public class ClusterServer {

	public void start() {
		Runnable task_1 = new PushServiceTask();
		Runnable task_2 = new PushTnsTask();

		Runnable task_3 = new CheckAndRemoveServiceTombstoneTask();

		/** 顺序执行 */
		ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
		pool.scheduleWithFixedDelay(task_1, 1, 5, TimeUnit.SECONDS);
		pool.scheduleWithFixedDelay(task_2, 3, 5, TimeUnit.SECONDS);

		pool.scheduleWithFixedDelay(task_3, 10, 10, TimeUnit.MINUTES);
	}
}
