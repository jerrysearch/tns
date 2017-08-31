package com.github.jerrysearch.tns.client.client;

import com.github.jerrysearch.tns.client.cluster.ClusterPool;
import com.github.jerrysearch.tns.client.conf.ClientConfig;
import com.github.jerrysearch.tns.client.indexbuild.IIndexBuilder;
import com.github.jerrysearch.tns.client.indexbuild.LoadbalanceTSNodeIndexBuilder;
import com.github.jerrysearch.tns.client.loadbalance.RandomTSNodeSelector;
import com.github.jerrysearch.tns.client.loadbalance.TNodeSelector;
import com.github.jerrysearch.tns.client.task.SysServiceListTask;
import com.github.jerrysearch.tns.client.task.TaskManager;
import com.github.jerrysearch.tns.protocol.rpc.TSNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServicePool extends ClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ServicePool.class);
    private final List<TSNode> serviceList = new ArrayList<TSNode>();
    private final TNodeSelector<TSNode> tsNodeSelector;
    private final IIndexBuilder<TSNode> indexBuilder;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    /**
     * @param clusterPool
     * @param serviceName
     * @param heartbeat   [10 , 60]
     */
    public ServicePool(ClusterPool clusterPool, String serviceName, int heartbeat) {
        this(clusterPool, serviceName, heartbeat, new RandomTSNodeSelector());
    }

    /**
     * @param clusterPool
     * @param serviceName
     * @param heartbeat      [10 , 60]
     * @param tsNodeSelector
     */
    public ServicePool(ClusterPool clusterPool, String serviceName, int heartbeat,
                       TNodeSelector<TSNode> tsNodeSelector) {
        this(clusterPool, serviceName, heartbeat, tsNodeSelector,
                new LoadbalanceTSNodeIndexBuilder());
    }

    /**
     * @param clusterPool
     * @param serviceName
     * @param heartbeat      [10 , 60]
     * @param tsNodeSelector
     * @param indexBuilder
     */
    public ServicePool(ClusterPool clusterPool, String serviceName, int heartbeat,
                       TNodeSelector<TSNode> tsNodeSelector, IIndexBuilder<TSNode> indexBuilder) {
        this.tsNodeSelector = tsNodeSelector;
        this.indexBuilder = indexBuilder;
        heartbeat = Math.max(10, heartbeat);
        heartbeat = Math.min(heartbeat, 60);
        this.registerService(clusterPool, serviceName, heartbeat);
    }

    /**
     * @param clusterPool
     * @param serviceName
     * @param heartbeat
     */
    private void registerService(ClusterPool clusterPool, String serviceName, int heartbeat) {
        String clientId = this.getClientId();
        SysServiceListTask task = new SysServiceListTask(clusterPool, this, serviceName, clientId);

        task.run(); // 初始提交任务，阻塞执行一次，以免getOne取不到可用节点

        TaskManager.getInstance().submit(task, heartbeat);
    }

    /**
     * 根据策略重建节点索引
     *
     * @param list
     * @see IIndexBuilder
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
     * @return TSNode
     * @see TNodeSelector
     */
    public TSNode getOne() {
        TSNode tsNode = null;
        try {
            this.readLock.lock();
            tsNode = this.tsNodeSelector.selectOne(this.serviceList);
        } finally {
            this.readLock.unlock();
        }
        return tsNode;
    }

    /**
     * node损坏
     *
     * @param tsNode
     */
    public void brokenNode(TSNode tsNode) {
        try {
            this.writeLock.lock();
            while (this.serviceList.remove(tsNode))
                ;
        } finally {
            this.writeLock.unlock();
        }
    }
}
