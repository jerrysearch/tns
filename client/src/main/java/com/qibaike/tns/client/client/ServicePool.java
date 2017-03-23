package com.qibaike.tns.client.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.tns.client.cluster.ClusterPool;
import com.qibaike.tns.client.conf.ClientConfig;
import com.qibaike.tns.client.indexbuild.IIndexBuilder;
import com.qibaike.tns.client.indexbuild.LoadbalanceTSNodeIndexBuilder;
import com.qibaike.tns.client.loadbalance.RandomTSNodeSelector;
import com.qibaike.tns.client.loadbalance.TNodeSelector;
import com.qibaike.tns.client.task.SysServiceListTask;
import com.qibaike.tns.client.task.TaskManager;
import com.qibaike.tns.protocol.rpc.TSNode;

public class ServicePool extends ClientConfig {

	private final List<TSNode> serviceList = new ArrayList<TSNode>();
	private final TNodeSelector<TSNode> tsnodeSelector;
	private final IIndexBuilder<TSNode> indexBuilder;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();
	private static final Logger log = LoggerFactory.getLogger(ServicePool.class);

	/**
	 * 
	 * @param clusterPool
	 * @param serviceName
	 * @param heartbeat
	 *            [10 , 60]
	 */
	public ServicePool(ClusterPool clusterPool, String serviceName, int heartbeat) {
		this(clusterPool, serviceName, heartbeat, new RandomTSNodeSelector());
	}

	/**
	 * 
	 * @param clusterPool
	 * @param serviceName
	 * @param heartbeat
	 *            [10 , 60]
	 * @param tsnodeSelector
	 */
	public ServicePool(ClusterPool clusterPool, String serviceName, int heartbeat,
			TNodeSelector<TSNode> tsnodeSelector) {
		this(clusterPool, serviceName, heartbeat, tsnodeSelector,
				new LoadbalanceTSNodeIndexBuilder());
	}

	/**
	 * 
	 * @param clusterPool
	 * @param clientId
	 * @param serviceName
	 * @param heartbeat
	 *            [10 , 60]
	 * @param tsnodeSelector
	 * @param indexBuilder
	 */
	public ServicePool(ClusterPool clusterPool, String serviceName, int heartbeat,
			TNodeSelector<TSNode> tsnodeSelector, IIndexBuilder<TSNode> indexBuilder) {
		this.tsnodeSelector = tsnodeSelector;
		this.indexBuilder = indexBuilder;
		heartbeat = Math.max(10, heartbeat);
		heartbeat = Math.min(heartbeat, 60);
		this.registerService(clusterPool, serviceName, heartbeat);
	}

	private void registerService(ClusterPool clusterPool, String serviceName, int heartbeat) {
		String clientId = this.getClientId();
		SysServiceListTask task = new SysServiceListTask(clusterPool, this, serviceName, clientId);

		task.run(); // 初始提交任务，阻塞执行一次，以免getOne取不到可用节点

		TaskManager.getInstance().submit(task, heartbeat);
	}

	/**
	 * 根据策略重建节点索引
	 * 
	 * @see IIndexBuilder
	 * @param list
	 */
	public void rebuildIndex(List<TSNode> list) {
		this.writeLock.tryLock();
		try {
			this.serviceList.clear();
			Collection<TSNode> collection = this.indexBuilder.build(list);
			this.serviceList.addAll(collection);
			log.debug("rebuildIndex : {}", Arrays.toString(list.toArray(new TSNode[list.size()])));
		} finally {
			this.writeLock.unlock();
		}
	}

	/**
	 * 根据提供策略选择一个节点
	 * 
	 * @see TNodeSelector
	 * @return
	 */
	public TSNode getOne() {
		TSNode tnode = null;
		try {
			this.readLock.lock();
			tnode = this.tsnodeSelector.selectOne(this.serviceList);
		} finally {
			this.readLock.unlock();
		}
		return tnode;
	}

	/**
	 * node损坏
	 * 
	 * @param tsnode
	 */
	public void brokenNode(TSNode tsnode) {
		try {
			this.writeLock.lock();
			while (this.serviceList.remove(tsnode))
				;
		} finally {
			this.writeLock.unlock();
		}
	}
}
