package io.nexusrpc.handler;

import io.nexusrpc.OperationInfo;
import io.nexusrpc.OperationUnsuccessfulException;
import org.jspecify.annotations.Nullable;

/** Handler for synchronous operation function. */
class SynchronousOperationHandler<T, R> implements OperationHandler<T, R> {
  private final SynchronousOperationFunction<T, R> function;

  SynchronousOperationHandler(SynchronousOperationFunction<T, R> function) {
    this.function = function;
  }

  @Override
  public OperationStartResult<R> start(
      OperationContext context, OperationStartDetails details, @Nullable T param)
      throws OperationUnsuccessfulException {
    return OperationStartResult.sync(function.apply(context, details, param));
  }

  @Override
  public @Nullable R fetchResult(OperationContext context, OperationFetchResultDetails details) {
    throw new UnsupportedOperationException("Not supported on sync operation");
  }

  @Override
  public OperationInfo fetchInfo(OperationContext context, OperationFetchInfoDetails details) {
    throw new UnsupportedOperationException("Not supported on sync operation");
  }

  @Override
  public void cancel(OperationContext context, OperationCancelDetails details) {
    throw new UnsupportedOperationException("Not supported on sync operation");
  }
}
