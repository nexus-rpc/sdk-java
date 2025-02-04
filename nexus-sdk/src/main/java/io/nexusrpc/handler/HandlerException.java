package io.nexusrpc.handler;

import org.jspecify.annotations.Nullable;

/** Thrown from a handler for any unexpected error. */
public class HandlerException extends RuntimeException {

  /**
   * RetryBehavior allows handlers to explicitly set the retry behavior of a {@link
   * HandlerException}. If not specified, retry behavior is determined from the error type. For
   * example {@link HandlerException.ErrorType#INTERNAL} is not retryable by default unless
   * specified otherwise.
   */
  public enum RetryBehavior {
    /**
     * Indicates the retry behavior for a {@link HandlerException} is determined by the {@link
     * HandlerException.ErrorType}.
     */
    UNSPECIFIED,
    /**
     * Indicates that a {@link HandlerException} should be retried, overriding the default retry
     * behavior of the {@link HandlerException.ErrorType}.
     */
    RETRYABLE,
    /**
     * Indicates that a {@link HandlerException} should not be retried, overriding the default retry
     * behavior of the {@link HandlerException.ErrorType}.
     */
    NON_RETRYABLE
  }

  private final ErrorType errorType;
  private final RetryBehavior retryBehavior;

  public HandlerException(ErrorType errorType, String message) {
    this(errorType, new RuntimeException(message), RetryBehavior.UNSPECIFIED);
  }

  public HandlerException(ErrorType errorType, String message, RetryBehavior retryBehavior) {
    this(errorType, new RuntimeException(message), retryBehavior);
  }

  public HandlerException(ErrorType errorType, @Nullable Throwable cause) {
    this(errorType, cause, RetryBehavior.UNSPECIFIED);
  }

  public HandlerException(
      ErrorType errorType, @Nullable Throwable cause, RetryBehavior retryBehavior) {
    super(cause == null ? "handler error" : "handler error: " + cause.getMessage(), cause);
    this.errorType = errorType;
    this.retryBehavior = retryBehavior;
  }

  /** Error type for this exception. */
  public ErrorType getErrorType() {
    return errorType;
  }

  /** Retry behavior for this exception. */
  public RetryBehavior getRetryBehavior() {
    return retryBehavior;
  }

  public boolean isRetryable() {
    if (retryBehavior != RetryBehavior.UNSPECIFIED) {
      return retryBehavior == RetryBehavior.RETRYABLE;
    }
    switch (errorType) {
      case BAD_REQUEST:
      case UNAUTHENTICATED:
      case UNAUTHORIZED:
      case NOT_FOUND:
      case NOT_IMPLEMENTED:
        return false;
      case RESOURCE_EXHAUSTED:
      case INTERNAL:
      case UNAVAILABLE:
      case UPSTREAM_TIMEOUT:
        return true;
      default:
        throw new IllegalStateException("Unknown error type: " + errorType);
    }
  }

  /** Error type that can occur on a handler exception. */
  public enum ErrorType {
    /**
     * The server cannot or will not process the request due to an apparent client error. Clients
     * should not retry this request unless advised otherwise.
     */
    BAD_REQUEST,
    /**
     * The client did not supply valid authentication credentials for this request. Clients should
     * not retry this request unless advised otherwise.
     */
    UNAUTHENTICATED,
    /**
     * The caller does not have permission to execute the specified operation. Clients should not
     * retry this request unless advised otherwise.
     */
    UNAUTHORIZED,
    /**
     * The requested resource could not be found but may be available in the future. Subsequent
     * requests by the client are permissible but not advised.
     */
    NOT_FOUND,
    /**
     * Some resource has been exhausted, perhaps a per-user quota, or perhaps the entire file system
     * is out of space. Subsequent requests by the client are permissible.
     */
    RESOURCE_EXHAUSTED,
    /** An internal error occurred. Subsequent requests by the client are permissible. */
    INTERNAL,
    /**
     * The server either does not recognize the request method, or it lacks the ability to fulfill
     * the request. Clients should not retry this request unless advised otherwise.
     */
    NOT_IMPLEMENTED,
    /** The service is currently unavailable. Subsequent requests by the client are permissible. */
    UNAVAILABLE,
    /**
     * Used by gateways to report that a request to an upstream server has timed out. Subsequent
     * requests by the client are permissible.
     */
    UPSTREAM_TIMEOUT
  }
}
