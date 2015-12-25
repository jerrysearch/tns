namespace java com.jerry.thriftnameserver.rpc

include "cluster.thrift"

service TNSRpc extends cluster.Cluster{

	/**
	*	获取一serviceName下可用的node列表
	*/
	list<cluster.TSNode> nodeList(1: string clientId, 2: string serviceName),
}