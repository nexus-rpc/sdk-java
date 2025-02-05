package io.nexusrpc.handler;

import io.nexusrpc.Link;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Result for {@link OperationHandler#start}.
 *
 * <p>This is either a synchronous result (created via {@link #sync}) or asynchronous operation
 * token (created via {@link #async}).
 */
public class OperationStartResult<R> {
  /** Create a builder with a synchronous result. */
  public static <R> Builder<R> newSyncBuilder(@Nullable R value) {
    return new Builder<R>().setSyncResult(value);
  }

  /** Create a builder with an async operation token. */
  public static <R> Builder<R> newAsyncBuilder(String operationToken) {
    return new Builder<R>().setAsyncOperationToken(operationToken);
  }

  /** Create a builder from an existing OperationStartResult. */
  public static <R> Builder<R> newBuilder(OperationStartResult<R> request) {
    return new Builder<R>(request);
  }

  /** Create a completed synchronous operation start result from the given value. */
  public static <R> OperationStartResult<R> sync(@Nullable R value) {
    return OperationStartResult.newSyncBuilder(value).build();
  }

  /** Create a started asynchronous operation start result with the given operation token. */
  public static <R> OperationStartResult<R> async(String operationToken) {
    return OperationStartResult.<R>newAsyncBuilder(operationToken).build();
  }

  private final @Nullable R syncResult;
  private final @Nullable String asyncOperationToken;
  private final List<Link> links;

  private OperationStartResult(
      @Nullable R syncResult, @Nullable String asyncOperationToken, List<Link> links) {
    this.syncResult = syncResult;
    this.asyncOperationToken = asyncOperationToken;
    this.links = links;
  }

  /** Whether this start result is synchronous or asynchronous. */
  public boolean isSync() {
    return asyncOperationToken == null;
  }

  /**
   * The synchronous result. This can be null if the result is actually null or it is an
   * asynchronous operation.
   */
  public @Nullable R getSyncResult() {
    return syncResult;
  }

  /** The asynchronous operation token. This will be null if the operation result is synchronous. */
  public @Nullable String getAsyncOperationToken() {
    return asyncOperationToken;
  }

  /** The links associated with the operation. */
  public List<Link> getLinks() {
    return links;
  }

  /** Builder for an OperationStartResult. */
  public static class Builder<R> {
    private R syncResult;
    private @Nullable String asyncOperationToken;
    private final List<Link> links;

    private Builder() {
      links = new ArrayList<>();
    }

    private Builder(OperationStartResult<R> result) {
      syncResult = result.syncResult;
      asyncOperationToken = result.asyncOperationToken;
      links = new ArrayList<>(result.links);
    }

    /**
     * Set the synchronous result.
     *
     * <p>Cannot be set if the asynchronous operation token is set.
     *
     * <p>NOTE: This method is intentionally private, users should use {@link #newSyncBuilder(R)} to
     * create a new builder.
     */
    private Builder<R> setSyncResult(R syncResult) {
      this.syncResult = syncResult;
      return this;
    }

    /**
     * Set the asynchronous operation token.
     *
     * <p>Cannot be set if the synchronous result is set.
     *
     * <p>NOTE: This method is intentionally private, users should use {@link
     * #newAsyncBuilder(String)} to create a new builder.
     */
    private Builder<R> setAsyncOperationToken(String asyncOperationToken) {
      if (asyncOperationToken == null || asyncOperationToken.isEmpty()) {
        throw new IllegalArgumentException("Operation Token cannot be null or empty");
      }
      this.asyncOperationToken = asyncOperationToken;
      return this;
    }

    /** Add a link to the operation. */
    public Builder<R> addLink(Link link) {
      links.add(link);
      return this;
    }

    /** Get links. */
    public List<Link> getLinks() {
      return links;
    }

    public OperationStartResult<R> build() {
      if (syncResult != null && asyncOperationToken != null) {
        throw new IllegalStateException("Cannot have both sync result and async operation token");
      }
      return new OperationStartResult<>(
          syncResult, asyncOperationToken, links != null ? links : Collections.emptyList());
    }
  }
}
