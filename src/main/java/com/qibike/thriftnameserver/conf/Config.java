package com.qibike.thriftnameserver.conf;

import com.qibike.thriftnameserver.rpc.clusterConstants;

public class Config {
	public static final String HOSTNAME = System.getProperty("hostname", "localhost");

	public static final Long TNSID = (long) (HOSTNAME+clusterConstants.PORT).hashCode();
	
	public static final Long serviceRemoveSeconds = 60L;
}
