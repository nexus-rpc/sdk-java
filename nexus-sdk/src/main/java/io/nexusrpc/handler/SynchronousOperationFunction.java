package io.nexusrpc.handler;

import io.nexusrpc.OperationException;
import org.jspecify.annotations.Nullable;

/**
 * Function interface for {@link OperationHandler#sync} representing a call made for every operation
 * call.
 */
@FunctionalInterface
public interface SynchronousOperationFunction<T, R> {
  /** Invoked every operation start call and expected to return a fixed/synchronous result. */
  @Nullable R apply(OperationContext context, OperationStartDetails details, @Nullable T param)
      throws OperationException;
}
