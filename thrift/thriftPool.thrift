namespace java com.jerry.thriftnameserver.rpc

service ThriftPool {

	/**
	*	获取一serviceName下可用的node列表
	*/
	list<TNode> nodeList(1: string clientId, 2: string serviceName)
}

struct TNode {
  1: required string host,
  2: required i32 port,
  3: required string instanceName,
  4: required i32 vNodes
}