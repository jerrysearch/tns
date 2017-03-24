package com.qibaike.tns.server.command;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.tns.server.util.DaemonThreadFactory;

public abstract class BaseSysCommand<V> implements Callable<V> {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4,
			new DaemonThreadFactory("BaseSysCommand"));

	protected final Logger log = LoggerFactory.getLogger(BaseSysCommand.class);
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
			V v = future.get(this.executionTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
			return v;
		} catch (ExecutionException | TimeoutException | CancellationException e) {
			// 任务执行异常、超时、被取消
			this.log.error("execute :", e);
			return this.getFallback();
		} catch (InterruptedException e) {
			// 线程被中断
			this.log.error("execute :", e);
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
