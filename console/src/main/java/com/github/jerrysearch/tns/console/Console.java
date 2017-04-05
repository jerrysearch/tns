package com.github.jerrysearch.tns.console;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.jerrysearch.tns.console.draw.ConsoleDraw;
import com.github.jerrysearch.tns.console.task.BaseClusterListTask;
import com.github.jerrysearch.tns.console.task.DispatchTakeAllLogEventScheduledTask;
import com.github.jerrysearch.tns.console.task.SyncAllClusterListScheduledTask;
import com.github.jerrysearch.tns.protocol.rpc.State;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;
import com.github.jerrysearch.tns.protocol.rpc.structConstants;

public class Console extends BaseClusterListTask {

	public static void main(String[] args) {
		/**
		 * 初始化
		 */
		String hostName = args[0];
		TCNode node = new TCNode();
		node.setHost(hostName);
		node.setPort(structConstants.PORT);
		node.setState(State.UP);
		updateAll(Arrays.asList(node));

		/**
		 * 打开定时任务
		 */
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		executor.scheduleWithFixedDelay(SyncAllClusterListScheduledTask.getInstance(), 0, 3,
				TimeUnit.SECONDS);
		executor.scheduleWithFixedDelay(DispatchTakeAllLogEventScheduledTask.getInstance(), 0, 3,
				TimeUnit.SECONDS);

		/**
		 * 开启控制台输出
		 */
		ConsoleDraw consoleDraw = new ConsoleDraw();
		consoleDraw.start();
	}

}
