package io.nexusrpc.client;

import io.nexusrpc.OperationInfo;
import java.util.concurrent.CompletableFuture;

/**
 * OperationHandle represents a handle to an operation that can be used to interact with the
 * operation.
 */
public interface OperationHandle<T> {
  String getOperationName();

  String getServiceName();

  String getOperationToken();

  OperationInfo getInfo(GetOperationInfoOptions options);

  void cancel(CancelOperationOptions options);

  T getResult(GetOperationResultOptions options);

  GetResultResponse<T> getResultWithDetails(GetOperationResultOptions options);

  CompletableFuture<OperationInfo> getInfoAsync(GetOperationInfoOptions options);

  CompletableFuture<Void> cancelAsync(CancelOperationOptions options);

  CompletableFuture<T> getResultAsync(GetOperationResultOptions options);

  CompletableFuture<GetResultResponse<T>> getResultWithDetailsAsync(
      GetOperationResultOptions options);

  // TODO Add no arg versions of the above methods that use default options.
}
