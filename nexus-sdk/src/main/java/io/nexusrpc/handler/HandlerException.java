package io.nexusrpc.handler;

import java.util.Arrays;
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

  private final String rawErrorType;
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
    this.rawErrorType = errorType.name();
    this.errorType = errorType;
    this.retryBehavior = retryBehavior;
  }

  public HandlerException(
      String rawErrorType, @Nullable Throwable cause, RetryBehavior retryBehavior) {
    super(cause == null ? "handler error" : "handler error: " + cause.getMessage(), cause);
    this.rawErrorType = rawErrorType;
    this.errorType =
        Arrays.stream(ErrorType.values()).anyMatch((t) -> t.name().equals(rawErrorType))
            ? ErrorType.valueOf(rawErrorType)
            : ErrorType.UNKNOWN;
    this.retryBehavior = retryBehavior;
  }

  /**
   * Get the raw error type. If the error type is not {@link ErrorType#UNKNOWN}, it will return the
   * string representation of the error type.
   */
  public String getRawErrorType() {
    return rawErrorType;
  }

  /**
   * Error type for this exception. If the error type is not recognized, it will return {@link
   * ErrorType#UNKNOWN}.
   */
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
      case CONFLICT:
      case NOT_IMPLEMENTED:
        return false;
      case RESOURCE_EXHAUSTED:
      case REQUEST_TIMEOUT:
      case INTERNAL:
      case UNAVAILABLE:
      case UPSTREAM_TIMEOUT:
      case UNKNOWN:
      default:
        return true;
    }
  }

  /** Error type that can occur on a handler exception. */
  public enum ErrorType {
    /** The error type is unknown. Subsequent requests by the client are permissible. */
    UNKNOWN,
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
     * The requested resource could not be found but may be available in the future. Clients should
     * not retry this request unless advised otherwise.
     */
    NOT_FOUND,
    /**
     * The request could not be made due to a conflict. This may happen when trying to create an
     * operation that has already been started. Clients should not retry this request unless advised
     * otherwise.
     */
    CONFLICT,
    /**
     * Returned by the server when it has given up handling a request. This may occur by enforcing a
     * client provided {@code Request-Timeout} or for any arbitrary reason such as enforcing some
     * configurable limit. Subsequent requests by the client are permissible.
     */
    REQUEST_TIMEOUT,
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
