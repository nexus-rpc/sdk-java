package io.nexusrpc.handler;

import org.jspecify.annotations.Nullable;

/**
 * Result for {@link OperationHandler#start}.
 *
 * <p>This is either a synchronous result (created via {@link #sync}) or asynchronous operation ID
 * (created via {@link #async}).
 */
public class OperationStartResult<R> {
  /** Create a completed synchronous operation start result from the given value. */
  public static <R> OperationStartResult<R> sync(@Nullable R value) {
    return new OperationStartResult<>(value, null);
  }

  /** Create a started asynchronous operation start result with the given operation ID. */
  public static <R> OperationStartResult<R> async(String operationId) {
    return new OperationStartResult<>(null, operationId);
  }

  private final @Nullable R syncResult;
  private final @Nullable String asyncOperationId;

  private OperationStartResult(@Nullable R syncResult, @Nullable String asyncOperationId) {
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
