package com.github.jerrysearch.tns.client.loadbalance;

import java.util.List;

/**
 * 节点选择器
 *
 * @param <T>
 * @author jerry
 */
public interface TNodeSelector<T> {

    T selectOne(List<T> list);
}
