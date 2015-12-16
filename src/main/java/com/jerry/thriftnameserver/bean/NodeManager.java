package com.jerry.thriftnameserver.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

	@Loggable
	public void putToDealyQueue(Node node) {
		DelayedNode delayedNode = new DelayedNode(node);
		delayQueue.put(delayedNode);
	}

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
		return this.onLine(serviceName, host, port, pingFrequency, instanceName);
	}

	@Override
	@Loggable
	public String onLine(String serviceName, String host, int port, long pingFrequency,
			String instanceName) {
		pingFrequency = Math.max(pingFrequency, 10); // 最小ping频率10秒
		pingFrequency = Math.min(pingFrequency, 60); // 最大ping频率1分钟

		Node node = new Node(serviceName, host, port, pingFrequency, instanceName);
		this.removeFromDelayQueue(serviceName, instanceName); // 加入之前，移除相同的实例
		this.putToDealyQueue(node);
		return node.toString();
	}

	@Override
	@Loggable
	public String offLine(String serviceName, String instanceName) {
		boolean a = this.removeFromDelayQueue(serviceName, instanceName);
		boolean b = this.removeFromMap(serviceName, instanceName);
		if (a && b) {
			return "OK !";
		} else if (a ^ b) {
			return "WARN !";
		} else {
			return "FAIL !";
		}
	}

	@Loggable
	private boolean removeFromDelayQueue(String serviceName, String instanceName) {
		Node dst = new Node(serviceName, "host", 0, 0L, instanceName);
		DelayedNode delayedNode = new DelayedNode(dst);
		return this.delayQueue.remove(delayedNode);
	}

	@Loggable
	private synchronized boolean removeFromMap(String serviceName, String instanceName) {
		if (this.useableMap.containsKey(serviceName)) {
			Map<String, Node> map = this.useableMap.get(serviceName);
			if (map.containsKey(instanceName)) {
				map.remove(instanceName);
				return true;
			}
		}
		return false;
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
			sb.append(node.toAllString()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public synchronized String listAll() {
		StringBuilder sb = new StringBuilder();
		Set<String> set = this.useableMap.keySet();
		for (String serviceName : set) {
			sb.append(serviceName).append(" : \n");
			String content = this.list(serviceName);
			sb.append(content);
		}
		return sb.toString();
	}

	@Loggable
	@Override
	public synchronized String clearAll() {
		this.delayQueue.clear();
		this.useableMap.clear();
		this.closedMap.clear();
		return "OK !";
	}

	private static class proxy {
		private static NodeManager nodeManager = new NodeManager();
	}

	public static NodeManager getInstance() {
		return proxy.nodeManager;
	}

	private final Map<String, DelayedNode> closedMap = new HashMap<String, DelayedNode>();

	@Override
	@Loggable
	public String openPing(String serviceName, String instanceName) {
		String key = serviceName + "_" + instanceName;
		if (this.closedMap.containsKey(key)) {
			DelayedNode delayedNode = this.closedMap.remove(key);
			/**
			 * 减小代码复杂度，不更新putTime，重新加入后会立即ping
			 */
			// delayedNode.setPutTime(System.currentTimeMillis());
			this.delayQueue.put(delayedNode);
			return "OK !";
		} else {
			return "EMPTY !";
		}
	}

	@Override
	@Loggable
	public String closePing(String serviceName, String instanceName) {
		DelayedNode dst = new DelayedNode(new Node(serviceName, "host", 0, 0L, instanceName));
		for (DelayedNode src : this.delayQueue) {
			if (src.equals(dst)) {
				String key = serviceName + "_" + instanceName;
				this.delayQueue.remove(src);
				this.closedMap.put(key, src);
				return "OK !";
			}
		}
		return "EMPTY !";
	}

	@Override
	public String listDelayQueue() {
		StringBuilder sb = new StringBuilder();
		for (DelayedNode delayedNode : this.delayQueue) {
			sb.append(delayedNode.getNode().toString()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public String listClosedMap() {
		Collection<DelayedNode> collection = this.closedMap.values();
		StringBuilder sb = new StringBuilder();
		for (DelayedNode delayedNode : collection) {
			sb.append(delayedNode.getNode().toString()).append("\n");
		}
		return sb.toString();
	}
}
