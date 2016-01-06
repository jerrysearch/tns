namespace java com.qibaike.thriftnameserver.rpc

include "cluster.thrift"

service TNSRpc extends cluster.Cluster{

	/**
	*	获取一serviceName下可用的node列表
	*/
	list<cluster.TSNode> serviceList(1: string clientId, 2: string serviceName),
	
	/**
	*	获取可用的cluster node列表
	*/
	list<cluster.TCNode> clusterList(1: string clientId)
}