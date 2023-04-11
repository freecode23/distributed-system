struct Result {
  1: string reqId
  2: i32 value,
  3: string status,
  4: string msg,
}

struct KeyValOperation {
  1: string propId,
  2: string opType,
  3: i32 key,
  4: i32 val,
}

struct Proposal {
  1: i32 propId,
  2: KeyValOperation operation
}

service KeyValueService {
  Result put(1: i32 key, 2: i32 value, 3: string reqId, 4:string ip, 5:i32 port),
  Result get(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  Result delete(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  Proposal accept(1: Proposal proposal)
}