namespace java com.jerry.thriftnameserver.rpc

const i32 PORT = 8700

service Cluster {

	/**
	*	TNS上线
	*/
	oneway void onLine(1: string host, 2: i32 port, 3: string id),
	
	/**
	*	获取所有服务列表
	*/
	list<SNode> allServiceList(1: string clientId)
}

struct TNSNode {
  1: required string host,
  2: required i32 port,
  3: required string instanceName,
  4: required i32 vNodes
}

struct SNode {
  1: required string host,
  2: required i32 port,
  3: required string instanceName,
  4: required i32 vNodes,
  5: required i64 pingFrequency,
  6: required string serviceName
}