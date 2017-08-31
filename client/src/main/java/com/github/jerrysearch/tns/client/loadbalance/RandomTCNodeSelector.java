package com.github.jerrysearch.tns.client.loadbalance;

import com.github.jerrysearch.tns.protocol.rpc.TCNode;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTCNodeSelector implements TNodeSelector<TCNode> {

    @Override
    public TCNode selectOne(List<TCNode> list) {
        if (list.isEmpty()) {
            return null;
        }
        int size = list.size();
        int tmp = ThreadLocalRandom.current().nextInt(size);
        return list.get(tmp);
    }
}
