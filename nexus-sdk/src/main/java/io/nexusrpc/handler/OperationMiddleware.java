package io.nexusrpc.handler;

/** Middleware for intercepting operations. */
public interface OperationMiddleware {

  /** Intercepts the given operation. Called once for each operation invocation. */
  OperationHandler<Object, Object> intercept(OperationHandler<Object, Object> next);
}
