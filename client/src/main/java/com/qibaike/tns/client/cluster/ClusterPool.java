package com.qibaike.tns.client.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.tns.client.conf.ClientConfig;
import com.qibaike.tns.client.indexbuild.IIndexBuilder;
import com.qibaike.tns.client.loadbalance.RandomTCNodeSelector;
import com.qibaike.tns.client.loadbalance.TNodeSelector;
import com.qibaike.tns.client.task.SysClusterListTask;
import com.qibaike.tns.client.task.TaskManager;
import com.qibaike.tns.protocol.rpc.TCNode;
import com.qibaike.tns.protocol.rpc.structConstants;

public class ClusterPool extends ClientConfig {
	private final TNodeSelector<TCNode> tcnodeSelector;

	private final List<TCNode> list = new ArrayList<TCNode>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();
	private static final Logger log = LoggerFactory.getLogger(ClusterPool.class);

	public ClusterPool(String... hosts) {
		this(new RandomTCNodeSelector(), hosts);
	}

	public ClusterPool(TNodeSelector<TCNode> tcnodeSelector, String... hosts) {
		this.tcnodeSelector = tcnodeSelector;
		this.init(hosts);
	}

	/**
	 * 用已知部分cluster节点来初始化
	 */
	private void init(String... hosts) {
		for (String host : hosts) {
			TCNode tcnode = new TCNode();
			tcnode.setHost(host);
			tcnode.setPort(structConstants.PORT);
			this.list.add(tcnode);
		}
		this.registerCluster();
	}

	/**
	 * 注册定时同步cluster list的任务
	 */
	private void registerCluster() {
		String clientId = this.getClientId();
		SysClusterListTask task = new SysClusterListTask(this, clientId);
		TaskManager.getInstance().submit(task, 10);
	}

	/**
	 * 根据提供策略选择一个节点
	 * 
	 * @see TNodeSelector
	 * @return
	 */
	public TCNode getOne() {
		try {
			this.readLock.lock();
			if (this.list.isEmpty()) {
				return null;
			}
			return this.tcnodeSelector.selectOne(this.list);
		} finally {
			this.readLock.unlock();
		}
	}

	/**
	 * 根据策略重建节点索引
	 * 
	 * @see IIndexBuilder
	 * @param list
	 */
	public void rebuildIndex(List<TCNode> list) {
		try {
			this.writeLock.lock();
			this.list.clear();
			this.list.addAll(list);
			log.debug("rebuildIndex : {}", Arrays.toString(list.toArray(new TCNode[list.size()])));
		} finally {
			this.writeLock.unlock();
		}
	}
}
