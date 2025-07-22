package io.nexusrpc.client;

import io.nexusrpc.OperationInfo;
import io.nexusrpc.client.transport.GetOperationInfoResponse;
import io.nexusrpc.client.transport.GetOperationResultResponse;
import io.nexusrpc.client.transport.Transport;
import java.util.concurrent.CompletableFuture;

/**
 * OperationHandle represents a handle to an operation that can be used to interact with the
 * operation.
 */
public class OperationHandle<T> {
  private final String operation;
  private final String service;
  private final String operationToken;
  private final Transport transport;

  public OperationHandle(
      String operation, String service, String operationToken, Transport transport) {
    this.operation = operation;
    this.service = service;
    this.operationToken = operationToken;
    this.transport = transport;
  }

  public String getOperation() {
    return operation;
  }

  public String getService() {
    return service;
  }

  String getOperationToken() {
    return operationToken;
  }

  OperationInfo getInfo(GetOperationInfoOptions options) {
    // TODO: translate options to transport options if necessary
    return transport
        .getOperationInfo(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationInfoOptions.newBuilder().build())
        .getOperationInfo();
  }

  void cancel(CancelOperationOptions options) {
    // TODO: translate options to transport options if necessary
    transport.cancelOperation(
        operation,
        service,
        operationToken,
        io.nexusrpc.client.transport.CancelOperationOptions.newBuilder().build());
  }

  T getResult(GetOperationResultOptions options) {
    // TODO: translate options to transport options if necessary
    return (T)
        transport
            .getOperationResult(
                operation,
                service,
                operationToken,
                io.nexusrpc.client.transport.GetOperationResultOptions.newBuilder().build())
            .getResult();
  }

  GetResultResponse<T> getResultWithDetails(GetOperationResultOptions options) {
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

  CompletableFuture<OperationInfo> getInfoAsync(GetOperationInfoOptions options) {
    return transport
        .getOperationInfoAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationInfoOptions.newBuilder().build())
        .thenApply(GetOperationInfoResponse::getOperationInfo);
  }

  CompletableFuture<Void> cancelAsync(CancelOperationOptions options) {
    return transport
        .cancelOperationAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.CancelOperationOptions.newBuilder().build())
        .thenApply(response -> null);
  }

  CompletableFuture<T> getResultAsync(GetOperationResultOptions options) {
    return transport
        .getOperationResultAsync(
            operation,
            service,
            operationToken,
            io.nexusrpc.client.transport.GetOperationResultOptions.newBuilder().build())
        .thenApply(response -> (T) response.getResult());
  }

  CompletableFuture<GetResultResponse<T>> getResultWithDetailsAsync(
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
