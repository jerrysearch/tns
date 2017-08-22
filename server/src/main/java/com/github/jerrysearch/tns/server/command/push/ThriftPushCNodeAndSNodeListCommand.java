package com.github.jerrysearch.tns.server.command.push;

import com.github.jerrysearch.tns.protocol.rpc.Cluster;
import com.github.jerrysearch.tns.protocol.rpc.State;
import com.github.jerrysearch.tns.protocol.rpc.TCNode;
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

public class ThriftPushCNodeAndSNodeListCommand extends BaseSysCommand<State> {

    private static final String CLUSTER_ID = Config.CLUSTER_ID;
    protected final TCNode tcnode;
    protected final List<TCNode> cList;
    protected final List<TSNode> sList;

    public ThriftPushCNodeAndSNodeListCommand(TCNode tcnode, List<TCNode> cList, List<TSNode> sList) {
        super();
        this.tcnode = tcnode;
        this.cList = cList;
        this.sList = sList;
    }

    @Override
    protected State run() throws Exception {
        String host = tcnode.getHost();
        int port = tcnode.getPort();

        TSocket transport = new TSocket(host, port, 1000);
        TProtocol protocol = new TBinaryProtocol(transport);
        Cluster.Client client = new Cluster.Client(protocol);
        try {
            transport.open();
            client.pushClusterAndServiceList(cList, sList);
        } finally {
            if (transport.isOpen()) {
                transport.close();
            }
        }
        return State.UP;
    }

    @Override
    protected State getFallback() {
        return this.getFallback(this.tcnode);
    }

    @Loggable(value = Loggable.WARN)
    protected State getFallback(TCNode tcnode) {
        switch (tcnode.getState()) {
            case DOWN_1:
                return State.DOWN_2;
            case DOWN_2:
                return State.DOWN;
            default:
                return State.DOWN_1;
        }
    }

    public State push() {
        long start = System.nanoTime();
        State state = this.push(tcnode);
        long end = System.nanoTime();
        this.summaryClusterPush(tcnode, state, (end - start) * 0.000001F);
        return state;
    }

    @Loggable
    private State push(TCNode tcnode) {
        return this.execute();
    }

    private void summaryClusterPush(TCNode tcnode, State state, float consume) {
        LogEvent event = new LogEvent();
        event.setSource(CLUSTER_ID);
        event.setOperation(Operation.SYNC_CAS);
        List<String> attributes = new LinkedList<String>();
        attributes.add("toCluster=" + tcnode.getHost());
        attributes.add("state=" + state.toString());
        attributes.add("consume(ms)=" + String.format("%.2f", consume));
        event.setAttributes(attributes);
        event.setTimestamp(System.currentTimeMillis());
        Summary.getInstance().appendLogEvent(event);
    }
}
