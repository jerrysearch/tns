package com.github.jerrysearch.tns.server.command;

import com.github.jerrysearch.tns.server.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public abstract class BaseSysCommand<V> implements Callable<V> {
    /**
     * 执行command的线程池
     */
    private static final ExecutorService COMMAND_EXECUTE_WORKER = Executors.newFixedThreadPool(2,
            new NamedThreadFactory("command_execute_worker", true));

    protected final Logger log = LoggerFactory.getLogger(BaseSysCommand.class);
    private final int executionTimeoutInMilliseconds;

    protected BaseSysCommand() {
        this(2000); // 默认超时2000毫秒
    }

    protected BaseSysCommand(int executionTimeoutInMilliseconds) {
        this.executionTimeoutInMilliseconds = executionTimeoutInMilliseconds;
    }

    public V execute() {
        Future<V> future = COMMAND_EXECUTE_WORKER.submit(this);
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
