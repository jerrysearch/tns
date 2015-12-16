package com.jerry.thriftnameserver.bean;

/**
 * thrift server node 实例
 * 
 * @author jerry pk( serviceName, instanceName )
 */
public class Node {
	private final String serviceName;
	private final String host;
	private final int port;
	private final String instanceName; // serviceName 的唯一标识
	private int vNodes;
	private final long pingFrequency;

	private boolean health = false;
	private long lastPingtTime = System.currentTimeMillis();

	public Node(String serviceName, String host, int port, long pingFrequency, String instanceName) {
		this.serviceName = serviceName;
		this.host = host;
		this.port = port;
		this.pingFrequency = pingFrequency;
		this.instanceName = instanceName;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public int getvNodes() {
		return vNodes;
	}

	public void setvNodes(int vNodes) {
		this.vNodes = vNodes;
	}

	public long getPingFrequency() {
		return pingFrequency;
	}

	public String getServiceName() {
		return serviceName;
	}

	public boolean isHealth() {
		return health;
	}

	public void setHealth(boolean health) {
		this.health = health;
	}

	public long getLastPingtTime() {
		return lastPingtTime;
	}

	public void setLastPingtTime(long lastPingtTime) {
		this.lastPingtTime = lastPingtTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
		result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (instanceName == null) {
			if (other.instanceName != null)
				return false;
		} else if (!instanceName.equals(other.instanceName))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [serviceName=" + serviceName + ", host=" + host + ", port=" + port
				+ ", instanceName=" + instanceName + ", pingFrequency=" + pingFrequency + "]";
	}

	public String toAllString() {
		return "Node [serviceName=" + serviceName + ", host=" + host + ", port=" + port
				+ ", instanceName=" + instanceName + ", vNodes=" + vNodes + ", pingFrequency="
				+ pingFrequency + ", health=" + health + ", lastPingtTime=" + lastPingtTime + "]";
	}
}
