package com.qibike.thriftnameserver.cluster;

/**
 * 没用了
 * @author jerry
 *
 */
@Deprecated
public class CNode {
	private String host;
	private int port;
	private String cid;
	private boolean isUp = false;	//dafault false
	private long timestamp;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public boolean isUp() {
		return isUp;
	}
	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
