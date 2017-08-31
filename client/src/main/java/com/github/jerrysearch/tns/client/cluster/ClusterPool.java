package com.github.jerrysearch.tns.client.cluster;

import com.github.jerrysearch.tns.client.conf.ClientConfig;
import com.github.jerrysearch.tns.client.indexbuild.IIndexBuilder;
import com.github.jerrysearch.tns.client.loadbalance.RandomTCNodeSelector;
import com.github.jerrysearch.tns.client.loadbalance.TNodeSelector;
import com.github.jerrysearch.tns.client.task.SysClusterListTask;
import com.github.jerrysearch.tns.client.task.TaskManager;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;
import com.github.jerrysearch.tns.protocol.rpc.structConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClusterPool extends ClientConfig {
    private static final Logger log = LoggerFactory.getLogger(ClusterPool.class);
    private final TNodeSelector<TCNode> tcNodeSelector;
    private final List<TCNode> list = new ArrayList<TCNode>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public ClusterPool(String... hosts) {
        this(new RandomTCNodeSelector(), hosts);
    }

    public ClusterPool(TNodeSelector<TCNode> tcNodeSelector, String... hosts) {
        this.tcNodeSelector = tcNodeSelector;
        this.init(hosts);
    }

    /**
     * 用已知部分cluster节点来初始化
     *
     * @param hosts
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
     * @return TCNode
     * @see TNodeSelector
     */
    public TCNode getOne() {
        try {
            this.readLock.lock();
            if (this.list.isEmpty()) {
                return null;
            }
            return this.tcNodeSelector.selectOne(this.list);
        } finally {
            this.readLock.unlock();
        }
    }

    /**
     * 根据策略重建节点索引
     *
     * @param list
     * @see IIndexBuilder
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
