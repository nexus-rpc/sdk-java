package io.nexusrpc.handler;

import io.nexusrpc.Experimental;

/** Middleware for intercepting operations. */
@Experimental
public interface OperationMiddleware {

  /** Intercepts the given operation. Called once for each operation invocation. */
  OperationHandler<Object, Object> intercept(
      OperationContext context, OperationHandler<Object, Object> next);
}
