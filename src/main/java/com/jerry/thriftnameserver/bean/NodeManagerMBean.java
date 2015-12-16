package com.jerry.thriftnameserver.bean;

public interface NodeManagerMBean {
	String onLine(String serviceName, String host, int port, long pingFrequency);

	String onLine(String serviceName, String host, int port, long pingFrequency, String instanceName);

	String offLine(String serviceName, String instanceName);
	
	String openPing(String serviceName, String instanceName);
	
	String closePing(String serviceName, String instanceName);

	String list(String serviceName);

	String listAll();
	
	String listDelayQueue();
	
	String listClosedMap();
	
	String clearAll();
}
