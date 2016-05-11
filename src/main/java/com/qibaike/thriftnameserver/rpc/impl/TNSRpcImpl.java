package com.qibaike.thriftnameserver.rpc.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;

import com.jcabi.aspects.Loggable;
import com.qibaike.thriftnameserver.cluster.CNodeManager;
import com.qibaike.thriftnameserver.rpc.TCNode;
import com.qibaike.thriftnameserver.rpc.TNSRpc.Iface;
import com.qibaike.thriftnameserver.rpc.TSNode;
import com.qibaike.thriftnameserver.service.SNodeManager;

public class TNSRpcImpl implements Iface {

	private final SNodeManager sNodeManager = SNodeManager.getInstance();
	private final CNodeManager cNodeManager = CNodeManager.getInstance();

	@Override
	@Loggable(skipResult = true)
	public List<TSNode> serviceList(String clientId, String serviceName) throws TException {
		List<TSNode> list = new LinkedList<TSNode>();
		this.sNodeManager.toUpServiceNodeList(serviceName, list);
		return list;
	}

	@Override
	@Loggable(skipResult = true)
	public List<TCNode> clusterList(String clientId) throws TException {
		List<TCNode> list = new LinkedList<TCNode>();
		this.cNodeManager.toUpClusterNodeList(list);
		return list;
	}
}
