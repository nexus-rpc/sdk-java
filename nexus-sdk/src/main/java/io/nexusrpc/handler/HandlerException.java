package io.nexusrpc.handler;

import org.jspecify.annotations.Nullable;

/** Thrown from a handler for any unexpected error. */
public class HandlerException extends RuntimeException {
  private final ErrorType errorType;

  public HandlerException(ErrorType errorType, String message) {
    this(errorType, new RuntimeException(message));
  }

  public HandlerException(ErrorType errorType, @Nullable Throwable cause) {
    super(cause == null ? "handler error" : "handler error: " + cause.getMessage(), cause);
    this.errorType = errorType;
  }

  /** Error type for this exception. */
  public ErrorType getErrorType() {
    return errorType;
  }

  /** Error type that can occur on a handler exception. */
  public enum ErrorType {
    /** The server cannot or will not process the request due to an apparent client error. */
    BAD_REQUEST,
    /** The client did not supply valid authentication credentials for this request. */
    UNAUTHENTICATED,
    /** The caller does not have permission to execute the specified operation. */
    UNAUTHORIZED,
    /**
     * The requested resource could not be found but may be available in the future. Subsequent
     * requests by the client are permissible.
     */
    NOT_FOUND,
    /**
     * Some resource has been exhausted, perhaps a per-user quota, or perhaps the entire file system
     * is out of space.
     */
    RESOURCE_EXHAUSTED,
    /** An internal error occurred */
    INTERNAL,
    /**
     * The server either does not recognize the request method, or it lacks the ability to fulfill
     * the request.
     */
    NOT_IMPLEMENTED,
    /**
     * The server either does not recognize the request method, or it lacks the ability to fulfill
     * the request.
     */
    UNAVAILABLE,
    /** Used by gateways to report that a request to an upstream server has timed out. */
    UPSTREAM_TIMEOUT
  }
}
