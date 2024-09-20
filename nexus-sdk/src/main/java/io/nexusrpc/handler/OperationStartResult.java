package io.nexusrpc.handler;

import io.nexusrpc.Link;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    return new OperationStartResult<>(null, new OperationStartResultAsync(operationId, null));
  }

  /** Create a started asynchronous operation start result with the given operation ID. */
  public static <R> OperationStartResult<R> async(String operationId, List<Link> links) {
    return new OperationStartResult<>(null, new OperationStartResultAsync(operationId, links));
  }

  private final @Nullable R syncResult;
  private final @Nullable OperationStartResultAsync asyncOperationResult;

  private OperationStartResult(
      @Nullable R syncResult, @Nullable OperationStartResultAsync asyncOperationResult) {
    this.syncResult = syncResult;
    this.asyncOperationResult = asyncOperationResult;
  }

  /** Whether this start result is synchronous or asynchronous. */
  public boolean isSync() {
    return asyncOperationResult == null;
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
    return asyncOperationResult != null ? asyncOperationResult.asyncOperationId : null;
  }

  /** The links associated with the asynchronous operation. */
  public List<Link> getLinks() {
    return asyncOperationResult != null ? asyncOperationResult.links : Collections.emptyList();
  }

  private static class OperationStartResultAsync {
    private final @Nullable String asyncOperationId;
    private final @Nullable List<Link> links;

    private OperationStartResultAsync(String asyncOperationId, List<Link> links) {
      this.asyncOperationId = asyncOperationId;
      this.links =
          Collections.unmodifiableList(
              new ArrayList(links != null ? links : Collections.emptyList()));
    }
  }
}
