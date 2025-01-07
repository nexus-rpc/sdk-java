package io.nexusrpc;

/** An operation has failed or was cancelled. */
public class OperationUnsuccessfulException extends Exception {
  private final OperationState state;

  private OperationUnsuccessfulException(OperationState state, Throwable cause) {
    super(cause);
    this.state = state;
  }

  public static OperationUnsuccessfulException Failure(Throwable cause) {
    return new OperationUnsuccessfulException(OperationState.FAILED, cause);
  }

  public static OperationUnsuccessfulException Cancelled(Throwable cause) {
    return new OperationUnsuccessfulException(OperationState.CANCELLED, cause);
  }

  public OperationState getState() {
    return state;
  }
}
