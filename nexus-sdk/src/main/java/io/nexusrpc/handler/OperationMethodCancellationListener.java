package io.nexusrpc.handler;

/**
 * Listener that can be registered to listen for cancellation of an operation handler method.
 *
 * <p>This is intentionally not marked with a {@link FunctionalInterface}, though it can be used
 * that way, to make clear that users may want an instance since the hash code of this is used to
 * remove listeners.
 */
public interface OperationMethodCancellationListener {
  /**
   * Called when an operation method is cancelled by the underlying RPC system. This should not
   * block.
   */
  void cancelled();
}
