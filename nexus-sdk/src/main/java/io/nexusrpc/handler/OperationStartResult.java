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
  /** Create a builder. */
  public static <R> Builder<R> newBuilder() {
    return new Builder<R>();
  }

  /** Create a builder from an existing OperationStartResult. */
  public static <R> Builder<R> newBuilder(OperationStartResult<R> request) {
    return new Builder<R>(request);
  }

  /** Create a completed synchronous operation start result from the given value. */
  public static <R> OperationStartResult<R> sync(@Nullable R value) {
    return OperationStartResult.<R>newBuilder().setSyncResult(value).build();
  }

  /** Create a started asynchronous operation start result with the given operation ID. */
  public static <R> OperationStartResult<R> async(String operationId) {
    return OperationStartResult.<R>newBuilder().setAsyncOperationId(operationId).build();
  }

  private final @Nullable R syncResult;
  private final @Nullable String asyncOperationId;
  private final List<Link> links;

  private OperationStartResult(
      @Nullable R syncResult, @Nullable String asyncOperationId, List<Link> links) {
    this.syncResult = syncResult;
    this.asyncOperationId = asyncOperationId;
    this.links = links;
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

  /** The links associated with the operation. */
  public List<Link> getLinks() {
    return links;
  }

  /** Builder for an OperationStartResult. */
  public static class Builder<R> {
    private R syncResult;
    private @Nullable String asyncOperationId;
    private @Nullable List<Link> links;

    private Builder() {
      links = new ArrayList<>();
    }

    private Builder(OperationStartResult<R> result) {
      syncResult = result.syncResult;
      asyncOperationId = result.asyncOperationId;
      links = result.links;
    }

    /**
     * Set the synchronous result.
     *
     * <p>Cannot be set if the asynchronous operation ID is set.
     */
    public Builder<R> setSyncResult(R syncResult) {
      this.syncResult = syncResult;
      return this;
    }

    /**
     * Set the asynchronous operation ID.
     *
     * <p>Cannot be set if the synchronous result is set.
     */
    public Builder<R> setAsyncOperationId(String asyncOperationId) {
      this.asyncOperationId = asyncOperationId;
      return this;
    }

    /** Add a link to the operation. */
    public Builder<R> addLink(Link link) {
      links.add(link);
      return this;
    }

    public OperationStartResult<R> build() {
      if (syncResult != null && asyncOperationId != null) {
        throw new IllegalStateException("Cannot have both sync result and async operation ID");
      }
      if (syncResult == null && asyncOperationId == null) {
        throw new IllegalStateException("Must have either sync result or async operation ID");
      }
      return new OperationStartResult<>(
          syncResult, asyncOperationId, links != null ? links : Collections.emptyList());
    }
  }
}
