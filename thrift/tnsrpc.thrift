namespace java com.jerry.thriftnameserver.rpc

include "cluster.thrift"

service TNSRpc extends cluster.Cluster{

	/**
	*	获取一serviceName下可用的node列表
	*/
	list<cluster.SNode> nodeList(1: string clientId, 2: string serviceName),
	
	
	/**
	*	获取一serviceName下所有(包含不可用)的node列表
	*/
	list<cluster.SNode> allNodeList(1: string clientId, 2: string serviceName)
}