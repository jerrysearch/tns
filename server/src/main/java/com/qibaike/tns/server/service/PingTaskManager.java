package com.qibaike.tns.server.service;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.jcabi.aspects.Loggable;
import com.qibaike.tns.protocol.rpc.TSNode;

public class PingTaskManager {
	/**
	 * 全部ping任务调度线程
	 */
	private final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(2);

	private PingTaskManager() {
		this.pool.setRemoveOnCancelPolicy(true);
	}

	@Loggable
	public void submit(TSNode tsnode) {
		PingTask task = new PingTask(tsnode);
		int pingFrequency = tsnode.getPingFrequency();
		/**
		 * 启动时间随机，避免同时增加大量任务，导致任务执行过于集中
		 */
		int startTime = ThreadLocalRandom.current().nextInt(pingFrequency);
		ScheduledFuture<?> future = this.pool.scheduleWithFixedDelay(task, startTime,
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
