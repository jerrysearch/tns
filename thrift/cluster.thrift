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
	oneway void pushServiceList(1: list<TSNode> sList),
	
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

struct TSNode {
  1: string host,
  2: i32 port,
  3: i64 id,
  4: i32 vNodes,
  5: i32 pingFrequency,
  6: string serviceName,
  7: STATE state,
  8: i64 timestamp
}