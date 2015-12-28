package com.qibike.thriftnameserver.service;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jcabi.aspects.Loggable;
import com.qibike.thriftnameserver.rpc.TSNode;

public class PingTaskManager {
	private final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(10);

	private PingTaskManager() {
		pool.setRemoveOnCancelPolicy(true);
	}

	@Loggable
	public void submit(TSNode tsnode) {
		PingTask task = new PingTask(tsnode);
		int pingFrequency = tsnode.getPingFrequency();
		ScheduledFuture<?> future = this.pool.scheduleWithFixedDelay(task, 0,
				pingFrequency, TimeUnit.SECONDS);
		task.setFuture(future);
	}

	private static class proxy {
		private static PingTaskManager instance = new PingTaskManager();
	}

	public static PingTaskManager getInstance() {
		return proxy.instance;
	}
}
