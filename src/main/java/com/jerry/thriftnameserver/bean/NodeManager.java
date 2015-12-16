package com.jerry.thriftnameserver.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcabi.aspects.Loggable;

public class NodeManager implements NodeManagerMBean {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final DelayQueue<DelayedNode> delayQueue = new DelayQueue<DelayedNode>();

	private final Map<String, Map<String, Node>> useableMap = new HashMap<String, Map<String, Node>>();

	private NodeManager() {
	}

	public void putToDealyQueue(Node node) {
		DelayedNode delayedNode = new DelayedNode(node);
		delayQueue.put(delayedNode);
	}

	@Loggable
	public Node take() {
		DelayedNode delayedNode = null;
		try {
			delayedNode = this.delayQueue.take();
			return delayedNode.getNode();
		} catch (InterruptedException e) {
			String threadName = Thread.currentThread().getName();
			log.error("Thread [{}] catch InterruptedException", threadName);
			return null;
		} finally {
			if (null == delayedNode) {
			} else {
				delayedNode.setPutTime(System.currentTimeMillis());
				this.delayQueue.put(delayedNode);
			}
		}
	}

	public void checkHealthOver(Node node, int vNodes) {
		if (vNodes < 1) {
			node.setHealth(false);
		} else {
			node.setHealth(true);
		}
		node.setvNodes(vNodes);
		node.setLastPingtTime(System.currentTimeMillis()); // 更新最后一次健康检查的时间
		this.updateMap(node, this.useableMap);
	}

	private synchronized void updateMap(Node node, Map<String, Map<String, Node>> nodeMap) {
		String serviceName = node.getServiceName();
		if (nodeMap.containsKey(serviceName)) {
			Map<String, Node> map = nodeMap.get(serviceName);
			map.put(node.getInstanceName(), node);
		} else {
			Map<String, Node> map = new HashMap<String, Node>();
			map.put(node.getInstanceName(), node);
			nodeMap.put(serviceName, map);
		}
	}

	@Override
	public String onLine(String serviceName, String host, int port, long pingFrequency) {
		String instanceName = String.valueOf(System.currentTimeMillis());
		int vNodes = 1;
		return this.onLine(serviceName, host, port, pingFrequency, instanceName, vNodes);
	}

	@Override
	public String onLine(String serviceName, String host, int port, long pingFrequency,
			String instanceName) {
		int vNodes = 1;
		return this.onLine(serviceName, host, port, pingFrequency, instanceName, vNodes);
	}

	@Override
	@Loggable
	public String onLine(String serviceName, String host, int port, long pingFrequency,
			String instanceName, int vNodes) {
		pingFrequency = Math.max(pingFrequency, 10); // 最小ping频率10秒
		pingFrequency = Math.min(pingFrequency, 60); // 最大ping频率1分钟

		vNodes = Math.max(vNodes, 1); // 最小虚拟节点数1
		vNodes = Math.min(vNodes, 10); // 最大虚拟节点数10

		Node node = new Node(serviceName, host, port, pingFrequency, instanceName);
		node.setvNodes(vNodes);
		this.putToDealyQueue(node);
		return node.toString();
	}

	@Override
	@Loggable
	public synchronized String offLine(String serviceName, String instanceName) {
		log.debug("delayQueue.size = ({})", delayQueue.size());
		Node dst = new Node(serviceName, "host", 0, 0L, instanceName);
		DelayedNode delayedNode = new DelayedNode(dst);
		this.delayQueue.remove(delayedNode);
		log.debug("delayQueue.size = ({})", delayQueue.size());

		Node node = null;
		Map<String, Node> map = null;
		if (this.useableMap.containsKey(serviceName)) {
			map = this.useableMap.get(serviceName);
			if (map.containsKey(instanceName)) {
				node = map.remove(instanceName);
			}
		}

		if (null == node) {
			/**
			 * 不存在
			 */
			return String.format("serviceName : [%s], instanceName : [%s] unExist", serviceName,
					instanceName);
		} else {
			return node.toString();
		}
	}

	@Override
	public synchronized String list(String serviceName) {
		if (!this.useableMap.containsKey(serviceName)) {
			return "EMPTY !";
		}
		Map<String, Node> map = this.useableMap.get(serviceName);
		if (map.isEmpty()) {
			return "EMPTY !";
		}
		Collection<Node> nodeList = map.values();
		StringBuilder sb = new StringBuilder();
		for (Node node : nodeList) {
			sb.append(node.toString()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public synchronized String listAll() {
		return "none";
	}

	private static class proxy {
		private static NodeManager nodeManager = new NodeManager();
	}

	public static NodeManager getInstance() {
		return proxy.nodeManager;
	}
}
