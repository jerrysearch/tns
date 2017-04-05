package com.github.jerrysearch.tns.server.app;

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

import com.github.jerrysearch.tns.protocol.rpc.TNSRpc;
import com.github.jerrysearch.tns.protocol.rpc.TNSRpc.Iface;
import com.github.jerrysearch.tns.server.rpc.impl.TNSRpcImpl;
import com.github.jerrysearch.tns.server.util.NamedThreadFactory;

public class TNSRpcServer {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public void start(final String host, final int port) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					TProcessor tprocessor = new TNSRpc.Processor<Iface>(new TNSRpcImpl());
					InetSocketAddress address = new InetSocketAddress(host, port);
					TServerSocket transport = new TServerSocket(address);
					TThreadPoolServer.Args ttArgs = new TThreadPoolServer.Args(transport);
					ttArgs.processor(tprocessor);
					ttArgs.protocolFactory(new TBinaryProtocol.Factory());
					ExecutorService executorService = Executors.newFixedThreadPool(4,
							new NamedThreadFactory("TNSRpcServer", false));
					ttArgs.executorService(executorService);
					TServer server = new TThreadPoolServer(ttArgs);
					server.serve();
				} catch (Exception e) {
					log.error("tns rpc serve", e);
				}
			}
		};

		Thread t = new Thread(runnable);
		t.setName("TNSRpcServer_Main");
		t.start();
	}
}
