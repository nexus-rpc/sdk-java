package io.nexusrpc.client;

import java.util.function.BiFunction;

public interface ServiceClient<T> {
  /**
   * Executes an operation on the Nexus service with the provided input. This method is synchronous
   * and returns the result directly.
   *
   * @param operation The operation method to execute, represented as a BiFunction.
   * @param input The input to the operation.
   * @return The result of the operation.
   */
  <U, R> R executeOperation(BiFunction<T, U, R> operation, U input);

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
      BiFunction<T, U, R> operation, U input, ExecuteOperationOptions options);

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   */
  <U, R> StartOperationResult<R> startOperation(BiFunction<T, U, R> operation, U input);

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   * @param options for start operations
   */
  <U, R> StartOperationResult<R> startOperation(
      BiFunction<T, U, R> operation, U input, StartOperationOptions options);

  /**
   * Gets a handle to an asynchronous operation.
   *
   * <p>Note: This does not perform validation that the token is valid.
   *
   * @param operation name of the operation.
   * @param token operation token.
   */
  OperationHandle<Object> newHandle(String operation, String token);

  /**
   * Gets a handle to an asynchronous operation.
   *
   * <p>Note: This does not perform validation that the token is valid.
   *
   * @param operation operation method.
   * @param token operation token.
   */
  <U, R> OperationHandle<R> newHandle(BiFunction<T, U, R> operation, String token);
}
