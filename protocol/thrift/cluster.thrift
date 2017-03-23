namespace java com.qibaike.tns.protocol.rpc

include "struct.thrift"
include "event.thrift"

service Cluster {

	/**
	*	cluster上线
	*/
	oneway void up(1: struct.TCNode cnode),
	
	/**
	*	一次性推送tns、service节点列表
	*/
	oneway void pushClusterAndServiceList(1: list<struct.TCNode> cList, 2: list<struct.TSNode> sList),
	
	/**
	*	获取所有service node列表
	*/
	list<struct.TSNode> allServiceList(1: string clientId),
	
	/**
	*	获取所有的cluster node列表
	*/
	list<struct.TCNode> clusterList(1: string clientId),
	
	/**
	*	取走所有用于log统计的event数据
	*/
	list<event.LogEvent> takeAllLogEvent(1: string clientId)
}
