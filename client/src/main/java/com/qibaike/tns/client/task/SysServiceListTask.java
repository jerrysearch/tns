package com.qibaike.tns.client.task;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.tns.client.client.ServicePool;
import com.qibaike.tns.client.cluster.ClusterPool;
import com.qibaike.tns.protocol.rpc.TCNode;
import com.qibaike.tns.protocol.rpc.TNSRpc;
import com.qibaike.tns.protocol.rpc.TSNode;

public class SysServiceListTask implements Runnable {

	private final ClusterPool clusterPool;
	private final ServicePool servicePool;
	private final String serviceName;
	private final String clientId;
	private static final Logger log = LoggerFactory.getLogger(SysServiceListTask.class);

	// private int checkCode = Integer.MIN_VALUE;

	public SysServiceListTask(ClusterPool clusterPool, ServicePool servicePool, String serviceName,
			String clientId) {
		this.clusterPool = clusterPool;
		this.servicePool = servicePool;
		this.serviceName = serviceName;
		this.clientId = clientId;
	}

	@Override
	public void run() {
		TCNode tcnode = this.clusterPool.getOne();
		if (null == tcnode) {
			return;
		}
		TSocket transport = null;
		try {
			transport = new TSocket(tcnode.getHost(), tcnode.getPort() + 1, 3000); // 默认超时3秒
			TProtocol protocol = new TBinaryProtocol(transport);
			TNSRpc.Client client = new TNSRpc.Client(protocol);
			transport.open();
			List<TSNode> list = client.serviceList(this.clientId, this.serviceName);
			this.servicePool.rebuildIndex(list);
			// int size = list.size();
			// int code = Integer.MAX_VALUE;
			// code = code ^ size;
			// for (TSNode tsnode : list) {
			// String key = tsnode.getHost() + tsnode.getPort();
			// code = code ^ key.hashCode();
			// }
			//
			// if (code != this.checkCode) {
			// this.checkCode = code;
			// thriftPool.rebuildIndex(list);
			// }
		} catch (Exception e) {
			log.warn("[{}] has one exception -> {}", tcnode.toString(), e.getMessage());
		} finally {
			if (null != transport && transport.isOpen()) {
				transport.close();
			}
		}
	}

	@Override
	public String toString() {
		return "SysServiceListTask [serviceName=" + serviceName + ", clientId=" + clientId + "]";
	}
}
