package io.nexusrpc.client.transport;

import io.nexusrpc.Experimental;
import io.nexusrpc.OperationInfo;

@Experimental
public class FetchOperationInfoResponse {
  /** Create a builder for FetchOperationInfoResponse. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final OperationInfo operationInfo;

  private FetchOperationInfoResponse(OperationInfo operationInfo) {
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

    public FetchOperationInfoResponse build() {
      return new FetchOperationInfoResponse(operationInfo);
    }
  }
}
