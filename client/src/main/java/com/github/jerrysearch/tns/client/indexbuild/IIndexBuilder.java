package com.github.jerrysearch.tns.client.indexbuild;

import java.util.Collection;
import java.util.List;

/**
 * 节点索引构建器
 *
 * @param <T>
 * @author jerry
 */
public interface IIndexBuilder<T> {

    Collection<T> build(List<T> list);
}
