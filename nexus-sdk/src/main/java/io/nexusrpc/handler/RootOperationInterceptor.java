package io.nexusrpc.handler;

import io.nexusrpc.OperationInfo;
import io.nexusrpc.OperationStillRunningException;
import io.nexusrpc.OperationUnsuccessfulException;
import org.jspecify.annotations.Nullable;

public class RootOperationInterceptor implements OperationInterceptor {
  private final OperationHandler<Object, Object> handler;

  public RootOperationInterceptor(OperationHandler<Object, Object> handler) {
    this.handler = handler;
  }

  @Override
  public OperationStartResult<Object> start(
      OperationContext context, OperationStartDetails details, @Nullable Object param)
      throws OperationUnsuccessfulException {
    return handler.start(context, details, param);
  }

  @Override
  public @Nullable Object fetchResult(OperationContext context, OperationFetchResultDetails details)
      throws OperationStillRunningException,
          OperationUnsuccessfulException,
          OperationHandlerException {
    return handler.fetchResult(context, details);
  }

  @Override
  public OperationInfo fetchInfo(OperationContext context, OperationFetchInfoDetails details)
      throws OperationHandlerException {
    return handler.fetchInfo(context, details);
  }

  @Override
  public void cancel(OperationContext context, OperationCancelDetails details)
      throws OperationHandlerException {
    handler.cancel(context, details);
  }
}
