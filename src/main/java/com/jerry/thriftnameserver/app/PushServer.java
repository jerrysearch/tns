package com.jerry.thriftnameserver.app;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jerry.thriftnameserver.cluster.CNodeManager;
import com.jerry.thriftnameserver.command.push.ThriftPushCNodeListCommand;
import com.jerry.thriftnameserver.rpc.STATE;
import com.jerry.thriftnameserver.rpc.TCNode;

public class PushServer {

	private final CNodeManager cNodeManager = CNodeManager.getInstance();

	public void start() {
		Runnable task_1 = this.getPushServiceTask();
		Runnable task_2 = this.getPushTnsTask();

		ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
		service.scheduleWithFixedDelay(task_1, 10, 10, TimeUnit.SECONDS);
		service.scheduleWithFixedDelay(task_2, 10, 10, TimeUnit.SECONDS);
	}

	/**
	 * 推送服务列表的任务
	 * 
	 * @return
	 */
	private Runnable getPushServiceTask() {
		Runnable task = new Runnable() {

			@Override
			public void run() {
			}
		};
		return task;
	}

	/**
	 * 推送tns列表的任务
	 * 
	 * @return
	 */
	private Runnable getPushTnsTask() {
		Runnable task = new Runnable() {

			@Override
			public void run() {
				TCNode tcnode = cNodeManager.getOne();
				if (null == tcnode) {
					return;
				}
				List<TCNode> list = cNodeManager.getList();
				ThriftPushCNodeListCommand command = new ThriftPushCNodeListCommand(tcnode, list);
				STATE state = command.push();
				/**
				 * 更新节点tcnode状态
				 */
				tcnode.setState(state);
				tcnode.setTimestamp(System.currentTimeMillis());
			}
		};
		return task;
	}
}
