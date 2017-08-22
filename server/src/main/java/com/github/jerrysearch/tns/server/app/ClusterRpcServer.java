package com.github.jerrysearch.tns.server.app;

import com.github.jerrysearch.tns.protocol.rpc.Cluster;
import com.github.jerrysearch.tns.protocol.rpc.Cluster.Iface;
import com.github.jerrysearch.tns.server.rpc.impl.ClusterRpcImpl;
import com.github.jerrysearch.tns.server.util.NamedThreadFactory;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClusterRpcServer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void start(final String host, final int port) {
        Runnable runnable = () -> {
            try {
                /*
                 * processor
				 */
                TProcessor tprocessor = new Cluster.Processor<Iface>(new ClusterRpcImpl());

				/*
                 * server transport
				 */
                InetSocketAddress address = new InetSocketAddress(host, port);
                TServerTransport tServerTransport = new TServerSocket(address);
                TThreadPoolServer.Args ttArgs = new TThreadPoolServer.Args(tServerTransport);
                ttArgs.processor(tprocessor);
                ttArgs.protocolFactory(new TBinaryProtocol.Factory());
                ExecutorService executorService = Executors.newFixedThreadPool(2,
                        new NamedThreadFactory("cluster_rpc_worker", false));
                ttArgs.executorService(executorService);
                TServer server = new TThreadPoolServer(ttArgs);
                server.serve();
            } catch (Exception e) {
                log.error("cluster rpc serve", e);
            }
        };

        Thread t = new Thread(runnable);
        t.setName("cluster_rpc_acceptor");
        t.start();
    }
}
