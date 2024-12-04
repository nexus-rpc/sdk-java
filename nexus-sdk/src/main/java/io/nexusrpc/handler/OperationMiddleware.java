package io.nexusrpc.handler;

/** Middleware for intercepting operations. */
public interface OperationMiddleware {

  /** Intercepts the given operation. Called once for each operation invocation. */
  OperationHandler<Object, Object> intercept(
      OperationContext context, OperationHandler<Object, Object> next);
}
