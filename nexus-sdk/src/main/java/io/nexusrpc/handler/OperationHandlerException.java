package io.nexusrpc.handler;

import io.nexusrpc.FailureInfo;
import org.jspecify.annotations.Nullable;

/** Thrown from a handler for any unexpected error. */
public class OperationHandlerException extends RuntimeException {
  private final ErrorType errorType;
  private final FailureInfo failureInfo;

  public OperationHandlerException(ErrorType errorType, String message) {
    this(errorType, message, null);
  }

  public OperationHandlerException(ErrorType errorType, String message, @Nullable Throwable cause) {
    this(errorType, FailureInfo.newBuilder().setMessage(message).build(), cause);
  }

  public OperationHandlerException(ErrorType errorType, FailureInfo failureInfo) {
    this(errorType, failureInfo, null);
  }

  public OperationHandlerException(
      ErrorType errorType, FailureInfo failureInfo, @Nullable Throwable cause) {
    super(failureInfo.getMessage(), cause);
    this.errorType = errorType;
    this.failureInfo = failureInfo;
  }

  /** Error type for this exception. */
  public ErrorType getErrorType() {
    return errorType;
  }

  /** Failure info, if any, for this exception. */
  public FailureInfo getFailureInfo() {
    return failureInfo;
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
    UNAVAILABLE,
    /** Used by gateways to report that a request to a downstream server has timed out. */
    DOWNSTREAM_TIMEOUT
  }
}
