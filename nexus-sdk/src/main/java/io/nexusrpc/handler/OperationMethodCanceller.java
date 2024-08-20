package io.nexusrpc.handler;

import java.util.LinkedHashSet;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Utility to cancel in-flight operation handler methods. Not to be confused with operation
 * cancellation.
 */
public class OperationMethodCanceller {
  // Expects to be synchronized on for all fields below
  private final Object lock = new Object();
  // Linked hash set to preserve insertion order
  private final Set<OperationMethodCancellationListener> listeners = new LinkedHashSet<>();
  private @Nullable String cancellationReason;

  void addListener(OperationMethodCancellationListener listener) {
    synchronized (lock) {
      // If already cancelled, invoke, don't add
      if (cancellationReason != null) {
        listener.cancelled();
      } else {
        listeners.add(listener);
      }
    }
  }

  void removeListener(OperationMethodCancellationListener listener) {
    synchronized (lock) {
      listeners.remove(listener);
    }
  }

  @Nullable String getCancellationReason() {
    synchronized (lock) {
      return cancellationReason;
    }
  }

  /**
   * Cancel the operation handler method. Cancel only applies the first time, all other cancel calls
   * are no-ops. This is not reentrant and therefore must not be called in a cancellation listener.
   */
  public void cancel(String reason) {
    synchronized (lock) {
      // Only set and invoke listeners if not already set
      if (cancellationReason == null) {
        cancellationReason = reason;
        listeners.forEach(OperationMethodCancellationListener::cancelled);
      }
    }
  }
}
