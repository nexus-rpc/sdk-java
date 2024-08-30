package io.nexusrpc.handler;

import io.nexusrpc.OperationInfo;
import io.nexusrpc.OperationStillRunningException;
import io.nexusrpc.OperationUnsuccessfulException;
import org.jspecify.annotations.Nullable;

/**
 * Handler for an operation.
 *
 * <p>An instance of this must be returned from an {@link OperationImpl} annotated method in a
 * {@link ServiceImpl}. Simple synchronous operations can use {@link #sync}, more complex or
 * asynchronous operations can manually implement this interface.
 *
 * @param <T> The parameter type of the operation. This can be {@link Void} for no parameter.
 * @param <R> the return type of the operation. This can be {@link Void} for no return.
 */
public interface OperationHandler<T, R> {
  /**
   * Create an operation handler for a synchronous operation backed by the given function. This
   * function will be called for each operation invocation. All other calls to the operation are not
   * supported.
   */
  static <T, R> OperationHandler<T, R> sync(SynchronousOperationFunction<T, R> func) {
    return new SynchronousOperationHandler<>(func);
  }

  /**
   * Handle the start of an operation.
   *
   * <p>The operation can be synchronous or asynchronous. Synchronous operations can return the
   * result via {@link OperationStartResult#sync}. Asynchronous operations can return the started
   * operation ID via {@link OperationStartResult#async}.
   *
   * @param context Context for the call.
   * @param details Details for the call.
   * @param param Parameter for the operation. This may be null if the parameter was not given.
   * @return The start result which is a synchronous value or an operation ID representing an
   *     asynchronously running operation.
   * @throws OperationUnsuccessfulException If thrown, can have failure details and state such as
   *     saying the operation was cancelled.
   * @throws OperationHandlerException Unexpected failures while running the handler.
   * @throws RuntimeException Any other exception, will be converted to an {@link
   *     OperationHandlerException} of type {@link OperationHandlerException.ErrorType#INTERNAL}.
   */
  OperationStartResult<R> start(
      OperationContext context, OperationStartDetails details, @Nullable T param)
      throws OperationUnsuccessfulException, OperationHandlerException;

  /**
   * Fetch the result for an asynchronously started operation.
   *
   * @param context Context for the call.
   * @param details Details for the call including the operation ID. The details also contain a
   *     timeout which affects how implementers should implement this function. See {@link
   *     OperationFetchResultDetails#getTimeout()} to see how to react to this value.
   * @return The resulting value upon success.
   * @throws OperationStillRunningException Operation is still running beyond the given timeout.
   * @throws OperationUnsuccessfulException Operation failed. If thrown, can have failure details
   *     and state such as saying the operation was cancelled.
   * @throws OperationHandlerException Unexpected failures while running the handler. This should be
   *     thrown with a type of {@link OperationHandlerException.ErrorType#NOT_FOUND} if the
   *     operation ID is not found.
   * @throws RuntimeException Any other exception, will be converted to an {@link
   *     OperationHandlerException} of type {@link OperationHandlerException.ErrorType#INTERNAL}.
   */
  @Nullable R fetchResult(OperationContext context, OperationFetchResultDetails details)
      throws OperationStillRunningException,
          OperationUnsuccessfulException,
          OperationHandlerException;

  /**
   * Fetch information about the asynchronously started operation.
   *
   * @param context Context for the call.
   * @param details Details for the call including the operation ID.
   * @return Information about the operation.
   * @throws OperationHandlerException Unexpected failures while running the handler. This should be
   *     thrown with a type of {@link OperationHandlerException.ErrorType#NOT_FOUND} if the
   *     operation ID is not found.
   * @throws RuntimeException Any other exception, will be converted to an {@link
   *     OperationHandlerException} of type {@link OperationHandlerException.ErrorType#INTERNAL}.
   */
  OperationInfo fetchInfo(OperationContext context, OperationFetchInfoDetails details)
      throws OperationHandlerException;

  /**
   * Cancel the asynchronously started operation.
   *
   * <p>This does not need to wait for cancellation to be processed, simply that cancellation is
   * delivered. Duplicate cancellation requests for an operation or cancellation requests for an
   * operation not running should just be ignored.
   *
   * @param context Context for the call.
   * @param details Details for the call including the operation ID.
   * @throws OperationHandlerException Unexpected failures while running the handler. This should be
   *     thrown with a type of {@link OperationHandlerException.ErrorType#NOT_FOUND} if the
   *     operation ID is not found.
   * @throws RuntimeException Any other exception, will be converted to an {@link
   *     OperationHandlerException} of type {@link OperationHandlerException.ErrorType#INTERNAL}.
   */
  void cancel(OperationContext context, OperationCancelDetails details)
      throws OperationHandlerException;
}
