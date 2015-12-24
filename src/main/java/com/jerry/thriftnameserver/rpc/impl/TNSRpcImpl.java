package com.jerry.thriftnameserver.rpc.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;

import com.jcabi.aspects.Loggable;
import com.jerry.thriftnameserver.bean.Node;
import com.jerry.thriftnameserver.bean.NodeManager;
import com.jerry.thriftnameserver.rpc.SNode;
import com.jerry.thriftnameserver.rpc.TNSRpc.Iface;

public class TNSRpcImpl implements Iface {

	private final NodeManager nodeManager = NodeManager.getInstance();

	@Override
	@Loggable(skipResult = true)
	public List<SNode> nodeList(String clientId, String serviceName) throws TException {
		return this.nodeList(serviceName, true);
	}

	@Override
	@Loggable(skipResult = true)
	public List<SNode> allNodeList(String clientId, String serviceName) throws TException {
		return this.nodeList(serviceName, false);
	}

	private List<SNode> nodeList(String serviceName, boolean check) {
		Collection<Node> collection = nodeManager.getServiceNodeList(serviceName);
		List<SNode> nodeList = new LinkedList<SNode>();
		for (Node node : collection) {
			if (check && !node.isHealth()) {
				continue;
			}

			SNode tNode = new SNode();
			tNode.setHost(node.getHost());
			tNode.setPort(node.getPort());
			tNode.setInstanceName(node.getInstanceName());
			tNode.setVNodes(node.getvNodes());
			nodeList.add(tNode);
		}
		return nodeList;
	}

	@Override
	@Loggable
	public void onLine(String host, int port, String id) throws TException {

	}

	@Override
	@Loggable(skipResult = true)
	public List<SNode> allServiceList(String clientId) throws TException {
		List<SNode> list = new LinkedList<SNode>();
		nodeManager.toServiceNodeList(list);
		return list;
	}
}
