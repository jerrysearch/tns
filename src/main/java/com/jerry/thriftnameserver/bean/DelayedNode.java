package com.jerry.thriftnameserver.bean;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class DelayedNode implements Delayed {
	private final Node node;

	public DelayedNode(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	/**
	 * 放入时间，单位毫秒
	 */
	private long putTime = System.currentTimeMillis();

	public void setPutTime(long putTime) {
		this.putTime = putTime;
	}

	private long getDealySeconds() {
		long tmp = (System.currentTimeMillis() - this.putTime) / 1000;
		return this.node.getPingFrequency() - tmp;
	}

	@Override
	public int compareTo(Delayed o) {
		if (o instanceof DelayedNode) {
			DelayedNode other = (DelayedNode) o;
			return Long.signum(this.getDealySeconds() - other.getDealySeconds());
		}
		return 1;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(this.getDealySeconds(), TimeUnit.SECONDS);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		DelayedNode other = (DelayedNode) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DelayedNode [node=" + node + ", putTime=" + putTime + "]";
	}
}