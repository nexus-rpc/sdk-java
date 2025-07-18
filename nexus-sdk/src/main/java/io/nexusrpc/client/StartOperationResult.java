package io.nexusrpc.client;

import io.nexusrpc.Link;
import java.util.List;
import org.jspecify.annotations.Nullable;

// TODO this can also be an interface with two implementations: one for synchronous and one for
// asynchronous operations.
public class StartOperationResult<T> {
  @Nullable T getResult() {
    return null;
  }

  @Nullable OperationHandle<T> getPendingOperationHandle() {
    return null;
  }

  List<Link> getLinks() {
    return null;
  }
}
