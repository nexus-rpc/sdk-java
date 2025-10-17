package io.nexusrpc.handler;

import io.nexusrpc.OperationException;

/** Top-level handler for service calls. */
public interface Handler {
  /**
   * Handle the start of an operation. See {@link OperationHandler#start} for operation details.
   *
   * <p>The implementation here should not close the input stream, that will be done by the caller.
   *
   * <p>If the result is an output stream, it will be closed later by the caller.
   */
  OperationStartResult<HandlerResultContent> startOperation(
      OperationContext context, OperationStartDetails details, HandlerInputContent input)
      throws OperationException, HandlerException;

  /**
   * Cancel the asynchronously started operation. See {@link OperationHandler#cancel} for details.
   */
  void cancelOperation(OperationContext context, OperationCancelDetails details)
      throws HandlerException;
}
