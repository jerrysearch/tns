package com.github.jerrysearch.tns.server.app.server;

import com.github.jerrysearch.tns.protocol.rpc.TNSRpc;
import com.github.jerrysearch.tns.protocol.rpc.TNSRpc.Iface;
import com.github.jerrysearch.tns.server.app.server.AbstractServer;
import com.github.jerrysearch.tns.server.rpc.impl.TNSRpcImpl;
import com.github.jerrysearch.tns.server.util.NamedThreadFactory;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TNSRpcServer extends AbstractServer {

    private final String host;
    private final int port;

    public TNSRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        Runnable runnable = () -> {
            try {
                TProcessor tprocessor = new TNSRpc.Processor<Iface>(new TNSRpcImpl());
                InetSocketAddress address = new InetSocketAddress(host, port);
                TServerSocket transport = new TServerSocket(address);
                TThreadPoolServer.Args ttArgs = new TThreadPoolServer.Args(transport);
                ttArgs.processor(tprocessor);
                ttArgs.protocolFactory(new TBinaryProtocol.Factory());
                ExecutorService executorService = Executors.newFixedThreadPool(4,
                        new NamedThreadFactory("service_rpc_worker", false));
                ttArgs.executorService(executorService);
                TServer server = new TThreadPoolServer(ttArgs);
                server.serve();
            } catch (Exception e) {
                log.error("tns rpc serve", e);
            }
        };
        Thread t = new Thread(runnable);
        t.setName("service_rpc_acceptor");
        t.start();
    }

    @Override
    public void shutdown() {

    }
}
