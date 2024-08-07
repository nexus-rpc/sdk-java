package io.nexusrpc;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiFunction;
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
  static <T, R> OperationHandler<T, R> sync(BiFunction<StartContext, @Nullable T, R> func) {
    return new OperationHandler<T, R>() {
      @Override
      public StartResult<R> start(StartContext ctx, @Nullable T param) {
        return StartResult.sync(func.apply(ctx, param));
      }

      @Override
      public R fetchResult(FetchResultContext ctx) {
        throw new UnsupportedOperationException("Not supported on sync operation");
      }

      @Override
      public OperationInfo fetchInfo(FetchInfoContext ctx) {
        throw new UnsupportedOperationException("Not supported on sync operation");
      }

      @Override
      public void cancel(CancelContext ctx) {
        throw new UnsupportedOperationException("Not supported on sync operation");
      }
    };
  }

  /**
   * Handle the start of an operation.
   *
   * <p>The operation can be synchronous or asynchronous. Synchronous operations can return the
   * result via {@link StartResult#sync}. Asynchronous operations can return the started operation
   * ID via {@link StartResult#async}.
   *
   * @param ctx Context containing details about the call.
   * @param param Parameter for the operation. This may be null if the parameter was not given.
   * @return The start result which is a synchronous value or an operation ID representing an
   *     asynchronously running operation.
   * @throws OperationUnsuccessfulException If thrown, can have failure details and state such as
   *     saying the operation was cancelled.
   * @throws RuntimeException Any other exception fails the operation start as an operation failure.
   */
  StartResult<R> start(StartContext ctx, @Nullable T param) throws OperationUnsuccessfulException;

  /**
   * Fetch the result for an asynchronously started operation.
   *
   * @param ctx Context containing details about the call including the operation ID. The context
   *     also contains a timeout which affects how implementers should implement this function. See
   *     {@link FetchResultContext#getTimeout()} to see how to react to this value.
   * @return The resulting value upon success.
   * @throws OperationNotFoundException Operation ID provided is not a known ID.
   * @throws OperationStillRunningException Operation is still running beyond the given timeout.
   * @throws OperationUnsuccessfulException Operation failed. If thrown, can have failure details
   *     and state such as saying the operation was cancelled.
   * @throws RuntimeException Any other exception is considered a failure to fetch the result.
   */
  R fetchResult(FetchResultContext ctx)
      throws OperationNotFoundException,
          OperationStillRunningException,
          OperationUnsuccessfulException;

  /**
   * Fetch information about the asynchronously started operation.
   *
   * @param ctx Context containing details about the call including the operation ID.
   * @return Information about the operation.
   * @throws OperationNotFoundException Operation ID provided is not a known ID.
   * @throws RuntimeException Any other exception is considered a failure to fetch the info.
   */
  OperationInfo fetchInfo(FetchInfoContext ctx) throws OperationNotFoundException;

  /**
   * Cancel the asynchronously started operation.
   *
   * <p>This does not need to wait for cancellation to be processed, simply that cancellation is
   * delivered. Duplicate cancellation requests for an operation or cancellation requests for an
   * operation not running should just be ignored.
   *
   * @param ctx Context containing details about the call including the operation ID.
   * @throws OperationNotFoundException Operation ID provided is not a known ID.
   */
  void cancel(CancelContext ctx) throws OperationNotFoundException;

  /**
   * Result for {@link #start}.
   *
   * <p>This is either a synchronous result (created via {@link #sync}) or asynchronous operation ID
   * (created via {@link #async}).
   */
  class StartResult<R> {
    /** Create a completed synchronous operation start result from the given value. */
    public static <R> StartResult<R> sync(R value) {
      return new StartResult<>(value, null);
    }

    /** Create a started asynchronous operation start result with the given operation ID. */
    public static <R> StartResult<R> async(String operationId) {
      return new StartResult<>(null, operationId);
    }

    private final @Nullable R syncResult;
    private final @Nullable String asyncOperationId;

    private StartResult(@Nullable R syncResult, @Nullable String asyncOperationId) {
      this.syncResult = syncResult;
      this.asyncOperationId = asyncOperationId;
    }

    /** Whether this start result is synchronous or asynchronous. */
    public boolean isSync() {
      return asyncOperationId == null;
    }

    /**
     * The synchronous result. This can be null if the result is actually null or it is an
     * asynchronous operation.
     */
    public @Nullable R getSyncResult() {
      return syncResult;
    }

    /** The asynchronous operation ID. This will be null if the operation result is synchronous. */
    public @Nullable String getAsyncOperationId() {
      return asyncOperationId;
    }
  }

  /** Context for {@link #start}. */
  interface StartContext {
    /** Service name. */
    String getService();

    /** Operation name. */
    String getOperation();

    /** Headers. Key accessing on this map is case-insensitive. */
    Map<String, String> getHeaders();

    /**
     * Optional callback for asynchronous operations to deliver results to. If this is present and
     * the implementation is an asynchronous operation, the implementation should ensure this
     * callback is invoked with the result upon completion.
     */
    @Nullable String getCallbackUrl();

    /** Headers to use on the callback if {@link #getCallbackUrl} is used. */
    Map<String, String> getCallbackHeaders();

    /** Unique request identifier from the caller to be used for deduplication. */
    String getRequestId();
  }

  /** Context for {@link #fetchResult}. */
  interface FetchResultContext {
    /** Service name. */
    String getService();

    /** Operation name. */
    String getOperation();

    /** Operation ID. */
    String getOperationId();

    /**
     * Optional timeout for how long the user want to wait on the result.
     *
     * <p>If this value is null, the result or {@link OperationStillRunningException} should be
     * returned/thrown right away. If this value is present, the fetch result call should try to
     * wait up until this duration or until an implementer chosen maximum, whichever ends sooner,
     * before returning the result or throwing {@link OperationStillRunningException}.
     */
    @Nullable Duration getTimeout();

    /** Headers. Key accessing on this map is case-insensitive. */
    Map<String, String> getHeaders();
  }

  /** Context for {@link #fetchInfo}. */
  interface FetchInfoContext {
    /** Service name. */
    String getService();

    /** Operation name. */
    String getOperation();

    /** Operation ID. */
    String getOperationId();

    /** Headers. Key accessing on this map is case-insensitive. */
    Map<String, String> getHeaders();
  }

  /** Context for {@link #cancel}. */
  interface CancelContext {
    /** Service name. */
    String getService();

    /** Operation name. */
    String getOperation();

    /** Operation ID. */
    String getOperationId();

    /** Headers. Key accessing on this map is case-insensitive. */
    Map<String, String> getHeaders();
  }
}
