namespace java com.github.jerrysearch.tns.protocol.rpc

include "struct.thrift"

service TNSRpc {

	/**
	*	获取一serviceName下可用的node列表
	*/
	list<struct.TSNode> serviceList(1: string clientId, 2: string serviceName),
	
	/**
	*	获取可用的cluster node列表
	*/
	list<struct.TCNode> clusterList(1: string clientId)
}