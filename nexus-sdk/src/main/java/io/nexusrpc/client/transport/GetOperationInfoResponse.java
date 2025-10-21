package io.nexusrpc.client.transport;

import io.nexusrpc.OperationInfo;

public class GetOperationInfoResponse {
  /** Create a builder for GetOperationInfoResponse. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final OperationInfo operationInfo;

  private GetOperationInfoResponse(OperationInfo operationInfo) {
    this.operationInfo = operationInfo;
  }

  public OperationInfo getOperationInfo() {
    return operationInfo;
  }

  public static class Builder {
    private OperationInfo operationInfo;

    private Builder() {}

    public Builder setOperationInfo(OperationInfo operationInfo) {
      this.operationInfo = operationInfo;
      return this;
    }

    public GetOperationInfoResponse build() {
      return new GetOperationInfoResponse(operationInfo);
    }
  }
}
