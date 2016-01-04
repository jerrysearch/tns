package com.qibike.thriftnameserver.app;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.qibike.thriftnameserver.cluster.CNodeManager;
import com.qibike.thriftnameserver.command.push.ThriftPushCNodeListCommand;
import com.qibike.thriftnameserver.command.push.ThriftPushSNodeListCommand;
import com.qibike.thriftnameserver.rpc.STATE;
import com.qibike.thriftnameserver.rpc.TCNode;
import com.qibike.thriftnameserver.rpc.TSNode;
import com.qibike.thriftnameserver.service.SNodeManager;

public class ClusterServer {

	private final CNodeManager cNodeManager = CNodeManager.getInstance();
	private final SNodeManager sNodeManager = SNodeManager.getInstance();

	public void start() {
		Runnable task_1 = this.getPushServiceTask();
		Runnable task_2 = this.getPushTnsTask();

		Runnable task_3 = this.getCheckAndRemoveServiceTombstoneTask();

		ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
		pool.scheduleWithFixedDelay(task_1, 10, 20, TimeUnit.SECONDS);
		pool.scheduleWithFixedDelay(task_2, 15, 20, TimeUnit.SECONDS);

		pool.scheduleWithFixedDelay(task_3, 1, 1, TimeUnit.MINUTES);
	}

	/**
	 * 检查节点下线，并移除墓碑任务
	 * 
	 * @return
	 */
	private Runnable getCheckAndRemoveServiceTombstoneTask() {
		Runnable task = new Runnable() {

			@Override
			public void run() {
				sNodeManager.CheckAndRemoveTombstone();
			}
		};
		return task;
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
				TCNode tcnode = cNodeManager.getOne();
				if (null == tcnode) {
					return;
				}
				List<TSNode> list = new LinkedList<TSNode>();
				sNodeManager.toAllServiceNodeList(list);
				if (list.isEmpty()) {
					return;
				}
				ThriftPushSNodeListCommand command = new ThriftPushSNodeListCommand(tcnode, list);
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
				List<TCNode> list = new LinkedList<TCNode>();
				cNodeManager.toAllClusterNodeList(list);
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
