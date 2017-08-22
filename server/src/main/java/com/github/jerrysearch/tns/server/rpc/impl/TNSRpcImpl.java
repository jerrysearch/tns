package com.github.jerrysearch.tns.server.rpc.impl;

import com.github.jerrysearch.tns.protocol.rpc.TCNode;
import com.github.jerrysearch.tns.protocol.rpc.TNSRpc.Iface;
import com.github.jerrysearch.tns.protocol.rpc.TSNode;
import com.github.jerrysearch.tns.protocol.rpc.event.LogEvent;
import com.github.jerrysearch.tns.protocol.rpc.event.Operation;
import com.github.jerrysearch.tns.server.cluster.CNodeManager;
import com.github.jerrysearch.tns.server.conf.Config;
import com.github.jerrysearch.tns.server.service.SNodeManager;
import com.github.jerrysearch.tns.server.summary.Summary;
import com.jcabi.aspects.Loggable;
import org.apache.thrift.TException;

import java.util.LinkedList;
import java.util.List;

public class TNSRpcImpl implements Iface {

    private final SNodeManager sNodeManager = SNodeManager.getInstance();
    private final CNodeManager cNodeManager = CNodeManager.getInstance();
    private final String clusterId = String.valueOf(Config.CLUSTER_ID);

    /**
     * 请求service列表
     */
    @Override
    @Loggable(skipResult = true)
    public List<TSNode> serviceList(String clientId, String serviceName) throws TException {
        List<TSNode> list = new LinkedList<TSNode>();
        this.sNodeManager.toUpServiceNodeList(serviceName, list);
        this.serviceListLogEvent(clientId, serviceName, list.size());
        return list;
    }

    /**
     * 上报LogEvent 用于统计
     *
     * @param clientId
     * @param serviceName
     * @see LogEvent
     */
    private void serviceListLogEvent(String clientId, String serviceName, int upNodes) {
        LogEvent event = new LogEvent();
        event.setSource(clientId);
        event.setOperation(Operation.SYNC_SERVICE);
        List<String> attributes = new LinkedList<String>();
        attributes.add("fromCluster=" + this.clusterId);
        attributes.add("sName=" + serviceName);
        attributes.add("upNodes=" + upNodes);
        event.setAttributes(attributes);
        event.setTimestamp(System.currentTimeMillis());
        Summary.getInstance().appendLogEvent(event);
    }

    /**
     * 请求cluster列表
     */
    @Override
    @Loggable(skipResult = true)
    public List<TCNode> clusterList(String clientId) throws TException {
        List<TCNode> list = new LinkedList<TCNode>();
        this.cNodeManager.toUpClusterNodeList(list);
        this.clusterListLogEvent(clientId, list.size());
        return list;
    }

    /**
     * 上报LogEvent 用于统计
     *
     * @param clientId
     * @see LogEvent
     */
    private void clusterListLogEvent(String clientId, int upNodes) {
        LogEvent event = new LogEvent();
        event.setSource(clientId);
        event.setOperation(Operation.SYNC_CLUSTER);
        List<String> attributes = new LinkedList<String>();
        attributes.add("fromCluster=" + this.clusterId);
        attributes.add("upNodes=" + upNodes);
        event.setAttributes(attributes);
        event.setTimestamp(System.currentTimeMillis());
        Summary.getInstance().appendLogEvent(event);
    }
}
