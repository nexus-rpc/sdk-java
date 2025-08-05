package io.nexusrpc.client.transport;

import io.nexusrpc.Experimental;
import io.nexusrpc.OperationException;
import io.nexusrpc.OperationStillRunningException;
import java.util.concurrent.CompletableFuture;

/** Transport interface for Nexus RPC operations. */
@Experimental
public interface Transport {
  StartOperationResponse startOperation(
      String operationName, String serviceName, Object input, StartOperationOptions options)
      throws OperationException;

  GetOperationResultResponse getOperationResult(
      String operationName,
      String serviceName,
      String operationToken,
      GetOperationResultOptions options)
      throws OperationException, OperationStillRunningException;

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

  CompleteOperationResponse completeOperation(
      String operationToken, CompleteOperationOptions options);

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

  CompletableFuture<CompleteOperationResponse> completeOperationAsync(
      String operationToken, CompleteOperationOptions options);
}
