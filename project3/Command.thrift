struct Result {
  1: string reqId
  2: i32 value,
  3: string msg,
}

enum PrepareStatus {
  OK = 1,
  KEY_LOCKED = 2,
  ERROR = 3,
}

struct PrepareResult {
  1: string reqId,
  2: PrepareStatus status,
  3: string msg,
}
service Command {
  Result put(1: i32 key, 2: i32 value, 3: string reqId, 4:string ip, 5:i32 port),
  Result get(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  Result delete(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  PrepareResult preparePut(1: i32 key, 2: i32 value, 3: string reqId),
  PrepareResult prepareGet(1: i32 key, 2: string reqId),
  PrepareResult prepareDelete(1: i32 key, 2: string reqId)
}