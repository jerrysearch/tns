package com.github.jerrysearch.tns.example;

import com.github.jerrysearch.tns.client.client.ServicePool;
import com.github.jerrysearch.tns.client.cluster.ClusterPool;
import com.github.jerrysearch.tns.protocol.rpc.TSNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientExample {

    private static final Logger log = LoggerFactory.getLogger(ClientExample.class);

    public static void main(String[] args) throws InterruptedException {

        if (null == args || args.length != 2) {
            log.info("please input server's hostname and service name");
            return;
        }

        // in class init
        ClusterPool clusterPool = new ClusterPool("hostname");
        ServicePool pool = new ServicePool(clusterPool, "serviceName", 10);

        // in method
        TSNode node = null;
        try {
            node = pool.getOne();
            // do something
        } catch (Exception e) {
            pool.brokenNode(node);
        }
    }

}
