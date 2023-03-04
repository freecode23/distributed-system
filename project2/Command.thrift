struct Result {
  1: string reqId
  2: i32 value,
  3: string msg,
}

service Command {
  Result put(1: i32 key, 2: i32 value, 3: string reqId),
  Result get(1: i32 key, 2: string reqId),
  Result delete(1: i32 key, 2: string reqId)
}