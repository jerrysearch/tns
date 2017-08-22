package com.github.jerrysearch.tns.server.command.ping;

import com.github.jerrysearch.tns.protocol.rpc.PoolAble;
import com.github.jerrysearch.tns.protocol.rpc.TSNode;
import com.github.jerrysearch.tns.protocol.rpc.event.LogEvent;
import com.github.jerrysearch.tns.protocol.rpc.event.Operation;
import com.github.jerrysearch.tns.server.command.BaseSysCommand;
import com.github.jerrysearch.tns.server.conf.Config;
import com.github.jerrysearch.tns.server.summary.Summary;
import com.jcabi.aspects.Loggable;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import java.util.LinkedList;
import java.util.List;

public class ThriftPingCommand extends BaseSysCommand<Integer> {

    private static final String CLUSTER_ID = Config.CLUSTER_ID;
    private final TSNode tsnode;

    public ThriftPingCommand(TSNode tsnode) {
        super();
        this.tsnode = tsnode;
    }

    @Loggable
    private int ping(TSNode tsnode) {
        return this.execute();
    }

    public int ping() {
        long start = System.nanoTime();
        int vNodes = this.ping(this.tsnode);
        long end = System.nanoTime();
        this.summaryServicePing(tsnode, vNodes, (end - start) * 0.000001F);
        return vNodes;
    }

    @Override
    protected Integer run() throws Exception {
        String host = this.tsnode.getHost();
        int port = this.tsnode.getPort();

        TSocket transport = new TSocket(host, port, 1000);
        TProtocol protocol = new TBinaryProtocol(transport);
        PoolAble.Client client = new PoolAble.Client(protocol);
        int vNodes = -1;
        try {
            transport.open();
            vNodes = client.ping();
        } finally {
            if (transport.isOpen()) {
                transport.close();
            }
        }
        return vNodes;
    }

    @Override
    protected Integer getFallback() {
        return this.getFallback(this.tsnode);
    }

    @Loggable(value = Loggable.WARN)
    private Integer getFallback(TSNode tsnode) {
        return -1;
    }

    /**
     * 汇总service节点ping数据
     *
     * @param tsnode  目标service节点
     * @param vNodes  虚拟节点个数
     * @param consume 耗费时间
     */
    private void summaryServicePing(TSNode tsnode, int vNodes, float consume) {
        LogEvent event = new LogEvent();
        event.setSource(CLUSTER_ID);
        event.setOperation(Operation.PING_SERVICE);
        List<String> attributes = new LinkedList<>();
        attributes.add("sName=" + tsnode.getServiceName());
        attributes.add("host=" + tsnode.getHost());
        attributes.add("port=" + tsnode.getPort());
        attributes.add("frequency=" + tsnode.getPingFrequency());
        attributes.add("vNodes=" + vNodes);
        attributes.add(String.format("consume(ms)=%.2f", consume));
        event.setAttributes(attributes);
        event.setTimestamp(System.currentTimeMillis());
        Summary.getInstance().appendLogEvent(event);
    }
}
