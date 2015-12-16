package com.jerry.thriftnameserver.bean;

public interface NodeManagerMBean {
	String onLine(String serviceName, String host, int port, long pingFrequency);

	String onLine(String serviceName, String host, int port, long pingFrequency, String instanceName);

	String onLine(String serviceName, String host, int port, long pingFrequency,
			String instanceName, int vNodes);

	String offLine(String serviceName, String instanceName);

	String list(String serviceName);

	String listAll();
	
	String clearAll();
}
