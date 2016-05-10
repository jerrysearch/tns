package com.qibaike.thriftnameserver.app;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qibaike.thriftnameserver.rpc.Cluster;
import com.qibaike.thriftnameserver.rpc.Cluster.Iface;
import com.qibaike.thriftnameserver.rpc.impl.ClusterRpcImpl;

public class ClusterRpcServer {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public void start(final String host, final int port) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					TProcessor tprocessor = new Cluster.Processor<Iface>(new ClusterRpcImpl());
					InetSocketAddress address = new InetSocketAddress(host, port);
					TServerSocket transport = new TServerSocket(address);
					TThreadPoolServer.Args ttArgs = new TThreadPoolServer.Args(transport);
					ttArgs.processor(tprocessor);
					ttArgs.protocolFactory(new TBinaryProtocol.Factory());
					ExecutorService executorService = Executors.newFixedThreadPool(2);
					ttArgs.executorService(executorService);
					TServer server = new TThreadPoolServer(ttArgs);
					server.serve();
				} catch (Exception e) {
					log.error("cluster rpc serve", e);
				}
			}
		};

		Thread t = new Thread(runnable);
		t.setName("ClusterRpcServer");
		t.start();
	}
}
