package io.nexusrpc.client;

import io.nexusrpc.Experimental;
import io.nexusrpc.Link;
import java.util.List;
import org.jspecify.annotations.Nullable;

// TODO this can also be an interface with two implementations: one for synchronous and one for
// asynchronous operations.
@Experimental
public class StartOperationResult<T> {
  private final T result;
  private final OperationHandle<T> handle;
  private final List<Link> links;

  public static <T> StartOperationResult<T> sync(T syncResult, List<Link> links) {
    return new StartOperationResult<>(syncResult, null, links);
  }

  public static <T> StartOperationResult<T> async(OperationHandle<T> handle, List<Link> links) {
    return new StartOperationResult<>(null, handle, links);
  }

  public StartOperationResult(T syncResult, OperationHandle<T> handle, List<Link> links) {
    this.result = syncResult;
    this.handle = handle;
    this.links = links;
  }

  public @Nullable T getResult() {
    return result;
  }

  public @Nullable OperationHandle<T> getPendingOperationHandle() {
    return handle;
  }

  public List<Link> getLinks() {
    return links;
  }
}
