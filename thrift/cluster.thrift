namespace java com.qibaike.thriftnameserver.rpc

include "struct.thrift"

const i32 PORT = 8700

service Cluster {

	/**
	*	TNS上线
	*/
	oneway void up(1: struct.TCNode cnode),
	
	/**
	*	一次性推送tns、service节点列表
	*/
	oneway void pushClusterAndServiceList(1: list<struct.TCNode> cList, 2: list<struct.TSNode> sList)
}
