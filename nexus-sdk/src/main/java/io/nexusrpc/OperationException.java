package io.nexusrpc;

/** An operation has failed or was canceled. */
public class OperationException extends Exception {
  private final OperationState state;

  private OperationException(OperationState state, Throwable cause) {
    super(cause);
    this.state = state;
  }

  public static OperationException failure(Throwable cause) {
    return new OperationException(OperationState.FAILED, cause);
  }

  public static OperationException canceled(Throwable cause) {
    return new OperationException(OperationState.CANCELED, cause);
  }

  public OperationState getState() {
    return state;
  }
}
