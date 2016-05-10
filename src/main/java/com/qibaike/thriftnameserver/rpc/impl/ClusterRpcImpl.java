package com.qibaike.thriftnameserver.rpc.impl;

import java.util.List;

import org.apache.thrift.TException;

import com.jcabi.aspects.Loggable;
import com.qibaike.thriftnameserver.cluster.CNodeManager;
import com.qibaike.thriftnameserver.rpc.Cluster.Iface;
import com.qibaike.thriftnameserver.rpc.TCNode;
import com.qibaike.thriftnameserver.rpc.TSNode;
import com.qibaike.thriftnameserver.service.SNodeManager;

public class ClusterRpcImpl implements Iface {

	private final SNodeManager sNodeManager = SNodeManager.getInstance();
	private final CNodeManager cNodeManager = CNodeManager.getInstance();

	@Loggable
	@Override
	public void up(TCNode tcnode) throws TException {
		this.cNodeManager.up(tcnode);
	}

	@Override
	public void pushClusterAndServiceList(List<TCNode> cList, List<TSNode> sList) throws TException {
		this.cNodeManager.pushClusterList(cList);
		this.sNodeManager.pushServiceList(sList);
	}
}
