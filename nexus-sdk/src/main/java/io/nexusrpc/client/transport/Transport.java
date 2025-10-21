package io.nexusrpc.client.transport;

import java.util.concurrent.CompletableFuture;

public interface Transport {
  StartOperationResponse startOperation(
      String operationName, String serviceName, StartOperationOptions options);

  GetOperationResultResponse getOperationResult(
      String operationName,
      String serviceName,
      String operationToken,
      GetOperationResultOptions options);

  GetOperationInfoResponse getOperationInfo(
      String operationName,
      String serviceName,
      String operationToken,
      GetOperationInfoOptions options);

  CancelOperationResponse cancelOperation(
      String operationName,
      String serviceName,
      String operationToken,
      CancelOperationOptions options);

  CompletableFuture<StartOperationResponse> startOperationAsync(
      String operationName,
      String serviceName,
      String operationToken,
      StartOperationOptions options);

  CompletableFuture<GetOperationResultResponse> getOperationResultAsync(
      String operationName,
      String serviceName,
      String operationToken,
      GetOperationResultOptions options);

  CompletableFuture<GetOperationInfoResponse> getOperationInfoAsync(
      String operationName,
      String serviceName,
      String operationToken,
      GetOperationInfoOptions options);

  CompletableFuture<CancelOperationResponse> cancelOperationAsync(
      String operationName,
      String serviceName,
      String operationToken,
      CancelOperationOptions options);
}
