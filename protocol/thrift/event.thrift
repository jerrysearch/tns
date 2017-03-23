namespace java com.qibaike.tns.protocol.rpc.event

	/**
	* 操作
	**/
	enum Operation {
	 SYNC_CLUSTER = 1,
	 SYNC_SERVICE = 2,
	 PING_SERVICE = 3,
	 SYNC_CAS = 4,
	 TAKE_LEVENT = 5
	}

	struct LogEvent {
	  1: string source,	// 来源（客户端标识或服务端ID）
	  2: Operation operation,	// 操作
	  3: list<string> attributes, // 操作属性
	  4: i64 timestamp	// 发生时间（所在服务器时间）
	}