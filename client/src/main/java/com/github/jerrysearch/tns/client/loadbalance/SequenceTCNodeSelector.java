package com.github.jerrysearch.tns.client.loadbalance;

import com.github.jerrysearch.tns.protocol.rpc.TCNode;

import java.util.List;

public class SequenceTCNodeSelector implements TNodeSelector<TCNode> {

    private int index = 0;

    @Override
    public TCNode selectOne(List<TCNode> list) {
        if (list.isEmpty()) {
            return null;
        }
        index++;
        int size = list.size();
        index = index % size;
        return list.get(index);
    }
}
