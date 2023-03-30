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

struct PrepareRequest {
  1: i32 proposalNumber
}

struct PrepareResponse {
  1: i32 highestProposalNumber,
  2: i32 acceptedValue
}

struct AcceptRequest {
  1: Proposal proposal
}

struct AcceptResponse {
  1: i32 proposalNumber
}

struct LearnRequest {
  1: Proposal proposal
}

service Command {
  Result put(1: i32 key, 2: i32 value, 3: string reqId, 4:string ip, 5:i32 port),
  Result get(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),
  Result delete(1: i32 key, 2: string reqId, 3:string ip, 4:i32 port),

  PrepareResponse prepare(1: PrepareRequest request),
  AcceptResponse accept(1: AcceptRequest request),
  void learn(1: LearnRequest request)
}