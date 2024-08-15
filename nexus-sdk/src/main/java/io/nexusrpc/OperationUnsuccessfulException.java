package io.nexusrpc;

/** An operation has failed or was cancelled. */
public class OperationUnsuccessfulException extends Exception {
  private final OperationState state;
  private final FailureInfo failureInfo;

  public OperationUnsuccessfulException(String message) {
    this(OperationState.FAILED, message);
  }

  public OperationUnsuccessfulException(OperationState state, String message) {
    this(state, FailureInfo.newBuilder().setMessage(message).build());
  }

  public OperationUnsuccessfulException(OperationState state, FailureInfo failureInfo) {
    super(failureInfo.getMessage());
    this.state = state;
    this.failureInfo = failureInfo;
  }

  public OperationState getState() {
    return state;
  }

  public FailureInfo getFailureInfo() {
    return failureInfo;
  }
}
