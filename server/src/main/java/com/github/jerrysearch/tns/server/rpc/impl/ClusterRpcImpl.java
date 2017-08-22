package com.github.jerrysearch.tns.server.rpc.impl;

import com.github.jerrysearch.tns.protocol.rpc.Cluster.Iface;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;
import com.github.jerrysearch.tns.protocol.rpc.TSNode;
import com.github.jerrysearch.tns.protocol.rpc.event.LogEvent;
import com.github.jerrysearch.tns.server.cluster.CNodeManager;
import com.github.jerrysearch.tns.server.service.SNodeManager;
import com.github.jerrysearch.tns.server.summary.Summary;
import com.jcabi.aspects.Loggable;
import org.apache.thrift.TException;

import java.util.LinkedList;
import java.util.List;

public class ClusterRpcImpl implements Iface {

    private final SNodeManager sNodeManager = SNodeManager.getInstance();
    private final CNodeManager cNodeManager = CNodeManager.getInstance();

    @Loggable
    @Override
    public void up(TCNode tcnode) throws TException {
        this.cNodeManager.up(tcnode);
    }

    @Loggable(skipArgs = true)
    @Override
    public void pushClusterAndServiceList(List<TCNode> cList, List<TSNode> sList) throws TException {
        this.cNodeManager.pushClusterList(cList);
        this.sNodeManager.pushServiceList(sList);
    }

    @Loggable(skipResult = true)
    @Override
    public List<TSNode> allServiceList(String clientId) throws TException {
        List<TSNode> list = new LinkedList<TSNode>();
        this.sNodeManager.toAllServiceNodeList(list);
        return list;
    }

    @Loggable(skipResult = true)
    @Override
    public List<TCNode> clusterList(String clientId) throws TException {
        List<TCNode> list = new LinkedList<TCNode>();
        this.cNodeManager.toAllClusterNodeList(list);
        return list;
    }

    @Loggable(skipResult = true)
    @Override
    public List<LogEvent> takeAllLogEvent(String clientId) throws TException {
        return Summary.getInstance().takeAllLogEvent();
    }
}
