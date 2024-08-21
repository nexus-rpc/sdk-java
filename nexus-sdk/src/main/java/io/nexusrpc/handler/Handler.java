package io.nexusrpc.handler;

import io.nexusrpc.OperationInfo;
import io.nexusrpc.OperationNotFoundException;
import io.nexusrpc.OperationStillRunningException;
import io.nexusrpc.OperationUnsuccessfulException;

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
      throws UnrecognizedOperationException, OperationUnsuccessfulException;

  /**
   * Fetch the result for an asynchronously started operation. See {@link
   * OperationHandler#fetchResult} for operation details.
   *
   * <p>If the result is an output stream, it will be closed later by the caller.
   */
  HandlerResultContent fetchOperationResult(
      OperationContext context, OperationFetchResultDetails details)
      throws UnrecognizedOperationException,
          OperationNotFoundException,
          OperationStillRunningException,
          OperationUnsuccessfulException;

  /**
   * Fetch information about the asynchronously started operation. See {@link
   * OperationHandler#fetchInfo} for details.
   */
  OperationInfo fetchOperationInfo(OperationContext context, OperationFetchInfoDetails details)
      throws UnrecognizedOperationException, OperationNotFoundException;

  /**
   * Cancel the asynchronously started operation. See {@link OperationHandler#cancel} for details.
   */
  void cancelOperation(OperationContext context, OperationCancelDetails details)
      throws UnrecognizedOperationException, OperationNotFoundException;
}
