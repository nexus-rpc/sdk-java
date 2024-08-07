package io.nexusrpc;

/** An operation has failed or was cancelled. */
public class OperationUnsuccessfulException extends Exception {
  private final OperationState state;
  private final OperationFailure failure;

  public OperationUnsuccessfulException(String message) {
    this(OperationState.FAILED, message);
  }

  public OperationUnsuccessfulException(OperationState state, String message) {
    this(state, OperationFailure.newBuilder().setMessage(message).build());
  }

  public OperationUnsuccessfulException(OperationState state, OperationFailure failure) {
    super(failure.getMessage());
    this.state = state;
    this.failure = failure;
  }

  public OperationState getState() {
    return state;
  }

  public OperationFailure getFailure() {
    return failure;
  }
}
