package com.github.jerrysearch.tns.console.task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jerrysearch.tns.protocol.rpc.State;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;

public class DispatchTakeAllLogEventScheduledTask extends BaseClusterListTask implements Runnable {

	private final Logger log = LoggerFactory.getLogger(DispatchTakeAllLogEventScheduledTask.class);

	private final ExecutorService executor = Executors.newFixedThreadPool(2);

	private DispatchTakeAllLogEventScheduledTask() {
	};

	@Override
	public void run() {
		try {
			List<TCNode> list = super.getAll();
			log.debug(Arrays.toString(list.toArray()));
			for (TCNode tcNode : list) {
				if (tcNode.getState() != State.UP) {
					continue;
				}
				TakeAllLogEventTask task = new TakeAllLogEventTask(tcNode);
				this.executor.submit(task);
			}
		} catch (Exception e) {
			log.error("", e);
		}

	}

	private static class proxy {
		private static DispatchTakeAllLogEventScheduledTask task = new DispatchTakeAllLogEventScheduledTask();
	}

	public static DispatchTakeAllLogEventScheduledTask getInstance() {
		return proxy.task;
	}

}
