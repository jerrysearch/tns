namespace java com.jerry.thriftnameserver.rpc

const i32 PORT = 8700

service Cluster {

	/**
	*	TNS上线
	*/
	oneway void up(1: TCNode cnode),
	
	/**
	*	推送服务节点列表
	*/
	oneway void pushServiceList(1: list<SNode> sList),
	
	/**
	*	推送tns节点列表
	*/
	oneway void pushClusterList(1: list<TCNode> cList)
}

enum STATE {
 UP = 1,
 DOWN = 2,
 Tombstone = 3
}

struct TCNode {
  1: string host,
  2: i32 port,
  3: i64 id,
  4: STATE state,
  5: i64 timestamp
}

struct SNode {
  1: required string host,
  2: required i32 port,
  3: required string instanceName,
  4: required i32 vNodes,
  5: required i64 pingFrequency,
  6: required string serviceName
}