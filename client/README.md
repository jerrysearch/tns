# tnsclient

[![Build Status](https://api.travis-ci.org/jerrysearch/tnsclient.svg)](https://travis-ci.org/jerrysearch/tnsclient)

tsnclient为thriftnameserver客户端

```
String clientId = "local_test";
ClusterPool clusterPool = new ClusterPool(clientId, "tns_1", "tns_2"...);
ThriftPool pool = new ThriftPool(clusterPool, clientId, <serviceName>, <heartbeat>);
TSNode tnode = pool.getOne();


/**
* 移除一个坏掉的节点
**/
pool.brokenNode(tsnode);
```
