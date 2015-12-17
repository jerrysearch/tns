package com.jerry.thriftnameserver.rpc.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;

import com.jcabi.aspects.Loggable;
import com.jerry.thriftnameserver.bean.Node;
import com.jerry.thriftnameserver.bean.NodeManager;
import com.jerry.thriftnameserver.rpc.TNode;
import com.jerry.thriftnameserver.rpc.ThriftPool.Iface;

public class ThriftPoolImpl implements Iface {

	private final NodeManager nodeManager = NodeManager.getInstance();
	@Override
	@Loggable(skipResult = true)
	public List<TNode> nodeList(String clientId, String serviceName) throws TException {
		Collection<Node> collection = nodeManager.getServiceNodeList(serviceName);
		List<TNode> nodeList = new LinkedList<TNode>();
		for(Node node : collection){
			if(!node.isHealth()){
				continue;
			}
			TNode tNode = new TNode();
			tNode.setHost(node.getHost());
			tNode.setPort(node.getPort());
			tNode.setInstanceName(node.getInstanceName());
			tNode.setVNodes(node.getvNodes());
			nodeList.add(tNode);
		}
		return nodeList;
	}
}
