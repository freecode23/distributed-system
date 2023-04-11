struct Result {
  1: string reqId
  2: i32 value,
  3: string status,
  4: string msg,
}

enum OperationType {
  PUT = 1,
  GET = 2,
  DELETE = 3
}


struct KeyValOperation {
  1: OperationType opType,
  2: i32 key,
  3: i32 val,
}

enum Status {
  ACCEPTED = 1,
  REJECTED = 2
}

struct Promise {
  1: Status status,
  2: Proposal proposal,
}

struct Proposal {
  1: i32 id,
  2: KeyValOperation operation
}

service KeyValueService {
  Result put(1: i32 key, 2: i32 value, 3: string reqId, 4:string ip, 5:i32 port),
  Result get(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  Result delete(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  Promise prepare(1: Proposal proposal),
  Proposal accept(1: Proposal proposal)
}