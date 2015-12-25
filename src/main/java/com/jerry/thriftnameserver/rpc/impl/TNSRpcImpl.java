package com.jerry.thriftnameserver.rpc.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;

import com.jcabi.aspects.Loggable;
import com.jerry.thriftnameserver.cluster.CNodeManager;
import com.jerry.thriftnameserver.rpc.TCNode;
import com.jerry.thriftnameserver.rpc.TNSRpc.Iface;
import com.jerry.thriftnameserver.rpc.TSNode;
import com.jerry.thriftnameserver.service.SNodeManager;

public class TNSRpcImpl implements Iface {

	private final SNodeManager nodeManager = SNodeManager.getInstance();
	private final CNodeManager cNodeManager = CNodeManager.getInstance();

	@Override
	@Loggable(skipResult = true)
	public List<TSNode> nodeList(String clientId, String serviceName) throws TException {
		List<TSNode> list = new LinkedList<TSNode>();
		this.nodeManager.toUpServiceNodeList(serviceName, list);
		return list;
	}

	@Override
	@Loggable
	public void up(TCNode tcnode) throws TException {
		this.cNodeManager.up(tcnode);
	}

	@Override
	@Loggable
	public void pushServiceList(List<TSNode> sList) throws TException {
		this.nodeManager.pushServiceList(sList);
	}

	@Override
	@Loggable
	public void pushClusterList(List<TCNode> cList) throws TException {
		this.cNodeManager.pushClusterList(cList);
	}
}
