package com.github.jerrysearch.tns.server.app.server;

import com.github.jerrysearch.tns.protocol.rpc.Cluster;
import com.github.jerrysearch.tns.protocol.rpc.Cluster.Iface;
import com.github.jerrysearch.tns.server.app.server.AbstractServer;
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

public class ClusterRpcServer extends AbstractServer{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String host;
    private final int port;
    public ClusterRpcServer(String host, int port){
        super();
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        Runnable runnable = () -> {
            try {
                TProcessor tprocessor = new Cluster.Processor<Iface>(new ClusterRpcImpl());
                InetSocketAddress address = new InetSocketAddress(this.host, this.port);
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

    @Override
    public void shutdown() {

    }
}
