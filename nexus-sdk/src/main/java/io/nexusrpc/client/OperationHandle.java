package io.nexusrpc.client;

import io.nexusrpc.*;
import io.nexusrpc.client.transport.FetchOperationInfoResponse;
import io.nexusrpc.client.transport.FetchOperationResultResponse;
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
    return getInfo(FetchOperationInfoOptions.newBuilder().build());
  }

  /**
   * Fetch information about the operation.
   *
   * @param options Options for the information retrieval.
   * @return Information about the operation.
   * @throws HandlerException Unexpected failures while running the handler..
   */
  public OperationInfo getInfo(FetchOperationInfoOptions options) {
    return transport
        .fetchOperationInfo(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.FetchOperationInfoOptions.newBuilder()
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
  public T fetchResult() throws OperationException, OperationStillRunningException {
    return fetchResult(FetchOperationResultOptions.newBuilder().build());
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
  public T fetchResult(FetchOperationResultOptions options)
      throws OperationException, OperationStillRunningException {
    FetchOperationResultResponse response =
        transport.fetchOperationResult(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.FetchOperationResultOptions.newBuilder()
                .setTimeout(options.getTimeout())
                .build());
    return (T) serializer.deserialize(response.getResult(), type);
  }

  public CompletableFuture<OperationInfo> fetchInfoAsync() {
    return fetchInfoAsync(FetchOperationInfoOptions.newBuilder().build());
  }

  public CompletableFuture<OperationInfo> fetchInfoAsync(FetchOperationInfoOptions options) {
    return transport
        .fetchOperationInfoAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.FetchOperationInfoOptions.newBuilder()
                .putAllHeaders(options.getHeaders())
                .build())
        .thenApply(FetchOperationInfoResponse::getOperationInfo);
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

  public CompletableFuture<T> fetchResultAsync() {
    return fetchResultAsync(FetchOperationResultOptions.newBuilder().build());
  }

  public CompletableFuture<T> fetchResultAsync(FetchOperationResultOptions options) {
    return transport
        .fetchOperationResultAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.FetchOperationResultOptions.newBuilder()
                .setTimeout(options.getTimeout())
                .build())
        .thenApply(res -> (T) serializer.deserialize(res.getResult(), type));
  }
}
