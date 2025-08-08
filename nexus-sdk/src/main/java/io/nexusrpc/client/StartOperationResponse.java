package io.nexusrpc.client;

import io.nexusrpc.Experimental;
import io.nexusrpc.Link;
import java.util.List;
import org.jspecify.annotations.Nullable;

// TODO this can also be an interface with two implementations: one for synchronous and one for
// asynchronous operations.
@Experimental
public interface StartOperationResponse<T> {
  class Sync<T> implements StartOperationResponse<T> {
    private final T result;
    private final @Nullable List<Link> links;

    public Sync(T result, @Nullable List<Link> links) {
      this.result = result;
      this.links = links;
    }

    public T getResult() {
      return result;
    }

    public @Nullable List<Link> getLinks() {
      return links;
    }
  }

  class Async<T> implements StartOperationResponse<T> {
    private final OperationHandle<T> handle;
    private final @Nullable List<Link> links;

    public Async(OperationHandle<T> handle, @Nullable List<Link> links) {
      this.handle = handle;
      this.links = links;
    }

    public OperationHandle<T> getHandle() {
      return handle;
    }

    public @Nullable List<Link> getLinks() {
      return links;
    }
  }
}
