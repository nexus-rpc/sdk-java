package io.nexusrpc.client;

import io.nexusrpc.OperationException;
import io.nexusrpc.client.transport.Transport;

/** CompletionClient is used to complete or fail async operations. */
public class CompletionClient {
  private final Transport transport;

  // TODO take CompleteOperationOptions as a parameter?
  public CompletionClient(Transport transport) {
    this.transport = transport;
  }

  /**
   * Complete an operation identified with the given token and result.
   *
   * @param token the operation token
   * @param result the result of the operation
   */
  void completeOperation(String token, Object result) {
    completeOperation(token, result, CompleteOperationOptions.newBuilder().build());
  }

  /**
   * Complete an operation identified with the given token and result.
   *
   * @param token the operation token
   * @param result the result of the operation
   * @param options additional options for completing the operation
   */
  void completeOperation(String token, Object result, CompleteOperationOptions options) {
    transport.completeOperation(
        token,
        io.nexusrpc.client.transport.CompleteOperationOptions.newBuilder()
            .setResult(result)
            .setOperationToken(token)
            .setLinks(options.getLinks())
            .setStartTime(options.getStartTime())
            .build());
  }

  /**
   * Fail an operation identified with the given token and result.
   *
   * @param token the operation token
   * @param error the error that caused the operation to fail
   */
  void failOperation(String token, OperationException error) {
    failOperation(token, error, CompleteOperationOptions.newBuilder().build());
  }

  /**
   * Fail an operation identified with the given token and result.
   *
   * @param token the operation token
   * @param error the error that caused the operation to fail
   * @param options additional options for failing the operation
   */
  void failOperation(String token, OperationException error, CompleteOperationOptions options) {
    transport.completeOperation(
        token,
        io.nexusrpc.client.transport.CompleteOperationOptions.newBuilder()
            .setError(error)
            .setOperationToken(token)
            .setLinks(options.getLinks())
            .setStartTime(options.getStartTime())
            .build());
  }
}
