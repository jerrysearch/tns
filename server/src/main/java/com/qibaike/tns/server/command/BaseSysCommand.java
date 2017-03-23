package com.qibaike.tns.server.command;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class BaseSysCommand<V> implements Callable<V> {
	private static final ThreadFactory threadFactory = null;
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4,
			threadFactory);

	private final int executionTimeoutInMilliseconds;

	public BaseSysCommand() {
		this(3000); // 默认超时3000毫秒
	}

	public BaseSysCommand(int executionTimeoutInMilliseconds) {
		this.executionTimeoutInMilliseconds = executionTimeoutInMilliseconds;
	}

	public V execute() {
		Future<V> future = BaseSysCommand.executorService.submit(this);
		try {
			V v = future.get(this.executionTimeoutInMilliseconds, TimeUnit.MICROSECONDS);
			return v;
		} catch (ExecutionException | TimeoutException | CancellationException e) {
			// 任务执行异常、超时、被取消
			return this.getFallback();
		} catch (InterruptedException e) {
			// 线程被中断
			return this.getFallback();
		}
	}

	@Override
	public V call() throws Exception {
		return this.run();
	}

	protected abstract V run() throws Exception;

	protected abstract V getFallback();

}
