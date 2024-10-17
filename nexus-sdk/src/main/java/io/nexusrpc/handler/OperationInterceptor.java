package io.nexusrpc.handler;

import io.nexusrpc.OperationInfo;
import io.nexusrpc.OperationStillRunningException;
import io.nexusrpc.OperationUnsuccessfulException;
import org.jspecify.annotations.Nullable;

/** OperationInterceptor intercepts the start, fetchResult, fetchInfo, and cancel of operations. */
public interface OperationInterceptor {

  /** Intercepts {@link OperationHandler#start(OperationContext, OperationStartDetails, Object)} */
  OperationStartResult<Object> start(
      OperationContext context, OperationStartDetails details, @Nullable Object param)
      throws OperationUnsuccessfulException;

  /**
   * Intercepts {@link OperationHandler#fetchResult(OperationContext, OperationFetchResultDetails)}
   */
  @Nullable Object fetchResult(OperationContext context, OperationFetchResultDetails details)
      throws OperationStillRunningException,
          OperationUnsuccessfulException,
          OperationHandlerException;

  /** Intercepts {@link OperationHandler#fetchInfo(OperationContext, OperationFetchInfoDetails)} */
  OperationInfo fetchInfo(OperationContext context, OperationFetchInfoDetails details)
      throws OperationHandlerException;

  /** Intercepts {@link OperationHandler#cancel(OperationContext, OperationCancelDetails)} */
  void cancel(OperationContext context, OperationCancelDetails details);
}
