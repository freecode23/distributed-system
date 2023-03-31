struct Result {
  1: string reqId
  2: i32 value,
  3: string status,
  4: string msg,
}

struct Proposal {
  1: i32 proposalNumber,
  2: i32 value
}

struct Request {
  1: i32 proposalNumber,
  2: i32 value,
  3: string reqType
}

struct Response {
  1: i32 highestProposalNumber,
  2: i32 acceptedValue
}

service Command {
  Result put(1: i32 key, 2: i32 value, 3: string reqId, 4:string ip, 5:i32 port),
  Result get(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  Result delete(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),

  Response prepare(1: Request prepareRequest),
  Response accept(1: Request acceptRequest),
  void learn(1: Request learnRequest)
}