package com.github.jerrysearch.tns.server.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SingleScheduledExecutorService implements ScheduledExecutorService {
	ScheduledExecutorService pool = Executors
			.newSingleThreadScheduledExecutor(new NamedThreadFactory("MainScheduleThread", false));

	private SingleScheduledExecutorService() {

	}

	private static class proxy {
		private static SingleScheduledExecutorService singleScheduledExecutorService = new SingleScheduledExecutorService();
	}

	public static SingleScheduledExecutorService getInstance() {
		return proxy.singleScheduledExecutorService;
	}

	@Override
	public void shutdown() {
		this.pool.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return this.pool.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return this.pool.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return this.pool.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.pool.awaitTermination(timeout, unit);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return this.pool.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return this.pool.submit(task, result);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return this.pool.submit(task);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		return this.pool.invokeAll(tasks);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
			TimeUnit unit) throws InterruptedException {
		return this.pool.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException,
			ExecutionException {
		return this.pool.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return this.pool.invokeAny(tasks, timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		this.pool.execute(command);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return this.pool.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return this.pool.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
			TimeUnit unit) {
		return this.pool.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
			long delay, TimeUnit unit) {
		return this.pool.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

}
