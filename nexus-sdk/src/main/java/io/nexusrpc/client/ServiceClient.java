package io.nexusrpc.client;

import io.nexusrpc.client.transport.GetOperationResultResponse;
import io.nexusrpc.client.transport.StartOperationResponse;
import io.nexusrpc.client.transport.Transport;
import java.util.function.BiFunction;

public class ServiceClient<T> {
  private final String service;
  private final Transport transport;

  // TODO maybe use the builder pattern for consistency
  public ServiceClient(String service, Transport transport) {
    this.service = service;
    this.transport = transport;
  }

  /**
   * Executes an operation on the Nexus service with the provided input. This method is synchronous
   * and returns the result directly.
   *
   * @param operation The operation method to execute, represented as a BiFunction.
   * @param input The input to the operation.
   * @return The result of the operation.
   */
  <U, R> R executeOperation(BiFunction<T, U, R> operation, U input) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * Executes an operation on the Nexus service with the provided input. This method is synchronous
   * and returns the result directly.
   *
   * @param operation The operation method to execute, represented as a BiFunction.
   * @param input The input to the operation.
   * @param options for execute operations
   * @return The result of the operation.
   */
  <U, R> R executeOperation(
      BiFunction<T, U, R> operation, U input, ExecuteOperationOptions options) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   */
  <U, R> StartOperationResult<R> startOperation(BiFunction<T, U, R> operation, U input) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   * @param options for start operations
   */
  <U, R> StartOperationResult<R> startOperation(
      BiFunction<T, U, R> operation, U input, StartOperationOptions options) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation name to start.
   * @param input The input to the operation.
   * @param options for start operations
   */
  <U, R> StartOperationResult<R> startOperation(
      String operation, U input, StartOperationOptions options) {
    // TODO translate options to transport options
    StartOperationResponse response =
        transport.startOperation(
            operation,
            service,
            io.nexusrpc.client.transport.StartOperationOptions.newBuilder().build());
    // TODO handle response and convert to StartOperationResult<R>
    return new StartOperationResult<>();
  }

  /**
   * Execute an operation on the Nexus service with the provided input.
   *
   * @param operation The operation name to start.
   * @param input The input to the operation.
   * @param options for start operations
   */
  <U, R> StartOperationResult<R> executeOperation(
      String operation, U input, ExecuteOperationOptions options) {
    // TODO translate options to transport options
    StartOperationResponse response =
        transport.startOperation(
            operation,
            service,
            io.nexusrpc.client.transport.StartOperationOptions.newBuilder().build());
    if (response.getSyncResult() != null) {
      // If the operation is synchronous, return the result directly
      return new StartOperationResult<>();
    }
    // TODO handle response and convert to GetOperationResultOptions<R>
    GetOperationResultResponse result =
        transport.getOperationResult(
            operation,
            service,
            response.getAsyncOperationToken(),
            io.nexusrpc.client.transport.GetOperationResultOptions.newBuilder().build());
    return new StartOperationResult<>();
  }

  /**
   * Gets a handle to an asynchronous operation.
   *
   * <p>Note: This does not perform validation that the token is valid.
   *
   * @param operation name of the operation.
   * @param token operation token.
   */
  OperationHandle<Object> newHandle(String operation, String token) {
    return new OperationHandle<>(operation, service, token, transport);
  }

  /**
   * Gets a handle to an asynchronous operation.
   *
   * <p>Note: This does not perform validation that the token is valid.
   *
   * @param operation operation method.
   * @param token operation token.
   */
  <U, R> OperationHandle<R> newHandle(BiFunction<T, U, R> operation, String token) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
