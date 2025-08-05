package io.nexusrpc.client;

import io.nexusrpc.*;
import io.nexusrpc.client.transport.GetOperationInfoResponse;
import io.nexusrpc.client.transport.GetOperationResultResponse;
import io.nexusrpc.client.transport.Transport;
import io.nexusrpc.handler.HandlerException;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

/**
 * OperationHandle represents a handle to an operation that can be used to interact with the
 * operation.
 */
@Experimental
public class OperationHandle<T> {
  private final String operation;
  private final String service;
  private final String operationToken;
  private final Type type;
  private final Serializer serializer;
  private final Transport transport;

  public OperationHandle(
      String operation,
      String service,
      String operationToken,
      Type type,
      Serializer serializer,
      Transport transport) {
    this.operation = operation;
    this.service = service;
    this.operationToken = operationToken;
    this.type = type;
    this.serializer = serializer;
    this.transport = transport;
  }

  /**
   * Get the operation name.
   *
   * @return The operation name.
   */
  public String getOperation() {
    return operation;
  }

  /**
   * Get the service name.
   *
   * @return The service name.
   */
  public String getService() {
    return service;
  }

  /**
   * Get the operation token.
   *
   * @return The operation token.
   */
  public String getOperationToken() {
    return operationToken;
  }

  /**
   * Fetch information about the operation.
   *
   * @return Information about the operation.
   * @throws HandlerException Unexpected failures while running the handler..
   */
  public OperationInfo getInfo() {
    return getInfo(GetOperationInfoOptions.newBuilder().build());
  }

  /**
   * Fetch information about the operation.
   *
   * @param options Options for the information retrieval.
   * @return Information about the operation.
   * @throws HandlerException Unexpected failures while running the handler..
   */
  public OperationInfo getInfo(GetOperationInfoOptions options) {
    return transport
        .getOperationInfo(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationInfoOptions.newBuilder()
                .putAllHeaders(options.getHeaders())
                .build())
        .getOperationInfo();
  }

  /**
   * Cancel the operation.
   *
   * <p>This does not need to wait for cancellation to be processed, simply that cancellation is
   * delivered. Duplicate cancellation requests for an operation or cancellation requests for an
   * operation not running should just be ignored.
   *
   * @throws HandlerException Unexpected failures while running the handler.
   */
  public void cancel() {
    cancel(CancelOperationOptions.newBuilder().build());
  }

  /**
   * Cancel the operation.
   *
   * <p>This does not need to wait for cancellation to be processed, simply that cancellation is
   * delivered. Duplicate cancellation requests for an operation or cancellation requests for an
   * operation not running should just be ignored.
   *
   * @param options Options for the cancellation.
   * @throws HandlerException Unexpected failures while running the handler.
   */
  public void cancel(CancelOperationOptions options) {
    transport.cancelOperation(
        operation,
        service,
        operationToken,
        io.nexusrpc.client.transport.CancelOperationOptions.newBuilder()
            .putAllHeaders(options.getHeaders())
            .build());
  }

  /**
   * Get the result for an operation.
   *
   * @return The resulting value upon success.
   * @throws OperationStillRunningException Operation is still running beyond the given timeout.
   * @throws OperationException Operation failed. If thrown, can have failure details and state such
   *     as saying the operation was cancelled.
   * @throws HandlerException Unexpected failures while running the handler.
   */
  public T getResult() throws OperationException, OperationStillRunningException {
    return getResult(GetOperationResultOptions.newBuilder().build());
  }

  /**
   * Get the result for an operation.
   *
   * @param options Options for the result retrieval
   * @return The resulting value upon success.
   * @throws OperationStillRunningException Operation is still running beyond the given timeout.
   * @throws OperationException Operation failed. If thrown, can have failure details and state such
   *     as saying the operation was cancelled.
   * @throws HandlerException Unexpected failures while running the handler.
   */
  public T getResult(GetOperationResultOptions options)
      throws OperationException, OperationStillRunningException {
    GetOperationResultResponse response =
        transport.getOperationResult(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationResultOptions.newBuilder()
                .setTimeout(options.getTimeout())
                .build());
    return (T) serializer.deserialize(response.getResult(), type);
  }

  public GetResultResponse<T> getResultWithDetails(GetOperationResultOptions options)
      throws OperationException, OperationStillRunningException {
    // TODO: translate options to transport options if necessary
    GetOperationResultResponse response =
        transport.getOperationResult(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationResultOptions.newBuilder().build());
    return GetResultResponse.<T>newBuilder()
        .setResult(response)
        .setLinks(response.getLinks())
        .build();
  }

  public CompletableFuture<OperationInfo> getInfoAsync() {
    return getInfoAsync(GetOperationInfoOptions.newBuilder().build());
  }

  public CompletableFuture<OperationInfo> getInfoAsync(GetOperationInfoOptions options) {
    return transport
        .getOperationInfoAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationInfoOptions.newBuilder()
                .putAllHeaders(options.getHeaders())
                .build())
        .thenApply(GetOperationInfoResponse::getOperationInfo);
  }

  public CompletableFuture<Void> cancelAsync() {
    return cancelAsync(CancelOperationOptions.newBuilder().build());
  }

  public CompletableFuture<Void> cancelAsync(CancelOperationOptions options) {
    return transport
        .cancelOperationAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.CancelOperationOptions.newBuilder()
                .putAllHeaders(options.getHeaders())
                .build())
        .thenApply(response -> null);
  }

  public CompletableFuture<T> getResultAsync() {
    return getResultAsync(GetOperationResultOptions.newBuilder().build());
  }

  public CompletableFuture<T> getResultAsync(GetOperationResultOptions options) {
    throw new UnsupportedOperationException("");
    //    return transport
    //        .getOperationResultAsync(
    //            operation,
    //            service,
    //            operationToken,
    //            io.nexusrpc.client.transport.GetOperationResultOptions.newBuilder().build())
    //        .thenApply(response -> (T) response.getResult());
  }

  public CompletableFuture<GetResultResponse<T>> getResultWithDetailsAsync(
      GetOperationResultOptions options) {
    return transport
        .getOperationResultAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationResultOptions.newBuilder().build())
        .thenApply(
            response ->
                GetResultResponse.<T>newBuilder()
                    .setResult(response)
                    .setLinks(response.getLinks())
                    .build());
  }

  // TODO Add no arg versions of the above methods that use default options.
}
