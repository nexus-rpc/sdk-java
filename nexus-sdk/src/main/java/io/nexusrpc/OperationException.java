package io.nexusrpc;

import org.jspecify.annotations.Nullable;

/** An operation has failed or was canceled. */
public class OperationException extends Exception {
  private final OperationState state;

  private OperationException(OperationState state, String message, @Nullable Throwable cause) {
    super(message, cause);
    this.state = state;
  }

  private OperationException(OperationState state, @Nullable Throwable cause) {
    super(cause);
    this.state = state;
  }

  /**
   * Create a failed operation exception with a message.
   *
   * @param message The failure message.
   * @return The operation exception.
   */
  public static OperationException failed(String message) {
    return new OperationException(OperationState.FAILED, message, null);
  }

  /**
   * Create a failed operation exception with a cause.
   *
   * @param cause The cause of the failure.
   * @return The operation exception.
   */
  public static OperationException failed(Throwable cause) {
    return new OperationException(OperationState.FAILED, cause);
  }

  /**
   * Create a failed operation exception with a message and cause.
   *
   * @param message The failure message.
   * @param cause The cause of the failure.
   * @return The operation exception.
   */
  public static OperationException failed(String message, Throwable cause) {
    return new OperationException(OperationState.FAILED, message, cause);
  }

  /**
   * Create a failed operation exception with a cause.
   *
   * @param cause The cause of the failure.
   * @return The operation exception.
   * @deprecated Use {@link #failed(Throwable)} instead.
   */
  @Deprecated
  public static OperationException failure(Throwable cause) {
    return failed(cause);
  }

  /**
   * Create a canceled operation exception with a message.
   *
   * @param message The cancellation message.
   * @return The operation exception.
   */
  public static OperationException canceled(String message) {
    return new OperationException(OperationState.CANCELED, message, null);
  }

  /**
   * Create a canceled operation exception with a cause.
   *
   * @param cause The cause of the cancellation.
   * @return The operation exception.
   */
  public static OperationException canceled(Throwable cause) {
    return new OperationException(OperationState.CANCELED, cause);
  }

  /**
   * Create a canceled operation exception with a message and cause.
   *
   * @param message The cancellation message.
   * @param cause The cause of the cancellation.
   * @return The operation exception.
   */
  public static OperationException canceled(String message, Throwable cause) {
    return new OperationException(OperationState.CANCELED, message, cause);
  }

  public OperationState getState() {
    return state;
  }
}
