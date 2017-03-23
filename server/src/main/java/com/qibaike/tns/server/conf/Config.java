package com.qibaike.tns.server.conf;

import com.qibaike.tns.protocol.rpc.structConstants;


public class Config {
	public static final String HOSTNAME = System.getProperty("hostname", "localhost");

	public static final Long TNSID = (long) (HOSTNAME + structConstants.PORT).hashCode();

	public static final Long serviceRemoveSeconds = 60L;
}
