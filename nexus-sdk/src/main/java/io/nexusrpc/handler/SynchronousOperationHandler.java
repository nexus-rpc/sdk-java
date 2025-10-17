package io.nexusrpc.handler;

import io.nexusrpc.OperationException;
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
      throws OperationException {
    return OperationStartResult.sync(function.apply(context, details, param));
  }

  @Override
  public void cancel(OperationContext context, OperationCancelDetails details) {
    throw new UnsupportedOperationException("Not supported on sync operation");
  }
}
