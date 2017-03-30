# tns client

```java
//	初始化ClusterPool。可共享给ServicePool，一个jvm一个即可
ClusterPool clusterPool = new ClusterPool("tns_1", "tns_2"...);

//	初始化ServicePool。一个service一个
ServicePool pool = new ServicePool(clusterPool, <serviceName>, <heartbeat>);

//	获取该pool下一个可用节点
TSNode tnode = pool.getOne();


// 移除一个坏掉的节点
pool.brokenNode(tsnode);
```
