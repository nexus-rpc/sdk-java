package io.nexusrpc.handler;

public interface ServiceHandlerInterceptor {

  /** Intercepts the given operation. Called once for each operation invocation. */
  OperationInterceptor interceptOperation(OperationInterceptor next);
}
