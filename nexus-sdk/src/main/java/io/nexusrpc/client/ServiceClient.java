package io.nexusrpc.client;

import io.nexusrpc.*;
import io.nexusrpc.client.transport.FetchOperationResultOptions;
import io.nexusrpc.client.transport.FetchOperationResultResponse;
import io.nexusrpc.client.transport.Transport;
import io.nexusrpc.handler.HandlerException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * ServiceClient is a generic client for Nexus services that allows you to execute and start
 * operations defined in a Nexus service definition. It provides methods to execute operations
 * synchronously or asynchronously, and get handles to ongoing operations.
 */
@Experimental
public class ServiceClient<T> {
  private final Class<T> serviceClass;
  private final ServiceDefinition serviceDefinition;
  private final Transport transport;
  private final Serializer serializer;

  public ServiceClient(ServiceClientOptions<T> options) {
    this.serviceClass = options.getServiceClass();
    this.serviceDefinition = options.getServiceDefinition();
    this.transport = options.getTransport();
    this.serializer = options.getSerializer();
  }

  /**
   * Executes an operation on the Nexus service with the provided input. This method is synchronous
   * and returns the result directly.
   *
   * @param operation The operation method to execute, represented as a BiFunction.
   * @param input The input to the operation.
   * @return The result of the operation.
   * @throws OperationStillRunningException if the operation is still running
   * @throws OperationException if the operation fails
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> R executeOperation(BiFunction<T, U, R> operation, U input)
      throws OperationException, OperationStillRunningException {
    return executeOperation(operation, input, ExecuteOperationOptions.newBuilder().build());
  }

  /**
   * Executes an operation on the Nexus service with the provided input. This method is synchronous
   * and returns the result directly.
   *
   * @param operation The operation method to execute, represented as a BiFunction.
   * @param input The input to the operation.
   * @param options for execute operations
   * @return The result of the operation.
   * @throws OperationStillRunningException if the operation is still running
   * @throws OperationException if the operation fails
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> R executeOperation(
      BiFunction<T, U, R> operation, U input, ExecuteOperationOptions options)
      throws OperationException, OperationStillRunningException {
    Objects.requireNonNull(operation, "operation");
    OperationDefinition operationDefinition = extractOperationDefinition(serviceClass, operation);
    return executeOperation(
        operationDefinition.getName(), operationDefinition.getOutputType(), input, options);
  }

  /**
   * Execute an operation on the Nexus service with the provided input.
   *
   * @param operation The operation name to start.
   * @param input The input to the operation.
   * @param options for start operations
   * @throws OperationStillRunningException if the operation is still running
   * @throws OperationException if the operation fails
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> R executeOperation(
      String operation, Type resultType, U input, ExecuteOperationOptions options)
      throws OperationException, OperationStillRunningException {
    Objects.requireNonNull(operation, "operation");
    Objects.requireNonNull(resultType, "resultType");
    Objects.requireNonNull(options, "options");
    io.nexusrpc.client.transport.StartOperationOptions.Builder transportOptions =
        io.nexusrpc.client.transport.StartOperationOptions.newBuilder()
            .setRequestId(options.getRequestId())
            .setCallbackURL(options.getCallbackURL())
            .setInboundLinks(options.getInboundLinks());

    options.getHeaders().forEach(transportOptions::putHeader);

    options.getCallbackHeaders().forEach(transportOptions::putCallbackHeader);

    io.nexusrpc.client.transport.StartOperationResponse response =
        transport.startOperation(
            operation, serviceDefinition.getName(), input, transportOptions.build());
    if (response.getAsyncOperationToken() == null) {
      // If the operation is synchronous, return the result directly
      return (R) serializer.deserialize(response.getSyncResult(), resultType);
    }
    FetchOperationResultResponse result =
        transport.fetchOperationResult(
            operation,
            serviceDefinition.getName(),
            response.getAsyncOperationToken(),
            FetchOperationResultOptions.newBuilder().setTimeout(options.getTimeout()).build());
    return (R) serializer.deserialize(result.getResult(), resultType);
  }

  /**
   * Executes an operation on the Nexus service with the provided input. This method is synchronous
   * and returns the result directly.
   *
   * @param operation The operation method to execute, represented as a BiFunction.
   * @param input The input to the operation.
   * @return CompletableFuture that will be completed with the result of the operation.
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> CompletableFuture<R> executeOperationAsync(BiFunction<T, U, R> operation, U input) {
    return executeOperationAsync(operation, input, ExecuteOperationOptions.newBuilder().build());
  }

  /**
   * Executes an operation on the Nexus service with the provided input. This method is synchronous
   * and returns the result directly.
   *
   * @param operation The operation method to execute, represented as a BiFunction.
   * @param input The input to the operation.
   * @param options for execute operations
   * @return The result of the operation.
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> CompletableFuture<R> executeOperationAsync(
      BiFunction<T, U, R> operation, U input, ExecuteOperationOptions options) {
    Objects.requireNonNull(operation, "operation");
    OperationDefinition operationDefinition = extractOperationDefinition(serviceClass, operation);
    return executeOperationAsync(
        operationDefinition.getName(), operationDefinition.getOutputType(), input, options);
  }

  /**
   * Execute an operation on the Nexus service with the provided input.
   *
   * @param operation The operation name to start.
   * @param input The input to the operation.
   * @param options for start operations
   */
  public <U, R> CompletableFuture<R> executeOperationAsync(
      String operation, Type resultType, U input, ExecuteOperationOptions options) {
    Objects.requireNonNull(operation, "operation");
    Objects.requireNonNull(resultType, "resultType");
    Objects.requireNonNull(options, "options");
    io.nexusrpc.client.transport.StartOperationOptions.Builder transportOptions =
        io.nexusrpc.client.transport.StartOperationOptions.newBuilder()
            .setRequestId(options.getRequestId())
            .setCallbackURL(options.getCallbackURL())
            .setInboundLinks(options.getInboundLinks());

    options.getHeaders().forEach(transportOptions::putHeader);

    options.getCallbackHeaders().forEach(transportOptions::putCallbackHeader);

    CompletableFuture<io.nexusrpc.client.transport.StartOperationResponse> response =
        transport.startOperationAsync(
            operation, serviceDefinition.getName(), input, transportOptions.build());
    return response.thenCompose(
        r -> {
          if (r.getAsyncOperationToken() == null) {
            // If the operation is synchronous, return the result directly
            return CompletableFuture.completedFuture(
                (R) serializer.deserialize(r.getSyncResult(), resultType));
          }
          CompletableFuture<FetchOperationResultResponse> result =
              transport.fetchOperationResultAsync(
                  operation,
                  serviceDefinition.getName(),
                  r.getAsyncOperationToken(),
                  FetchOperationResultOptions.newBuilder()
                      .setTimeout(options.getTimeout())
                      .build());
          return result.thenApply(res -> (R) serializer.deserialize(res.getResult(), resultType));
        });
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   * @throws OperationException if the operation fails
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> StartOperationResponse<R> startOperation(BiFunction<T, U, R> operation, U input)
      throws OperationException {
    return startOperation(operation, input, StartOperationOptions.newBuilder().build());
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   * @param options for start operations
   * @throws OperationException if the operation fails
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> StartOperationResponse<R> startOperation(
      BiFunction<T, U, R> operation, U input, StartOperationOptions options)
      throws OperationException {
    OperationDefinition operationDefinition = extractOperationDefinition(serviceClass, operation);
    return startOperation(
        operationDefinition.getName(), operationDefinition.getOutputType(), input, options);
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation name to start.
   * @param input The input to the operation.
   * @param options for start operations
   * @throws OperationException if the operation fails
   * @throws HandlerException if there is an unexpected failure while running the handler
   */
  public <U, R> StartOperationResponse<R> startOperation(
      String operation, Type resultType, U input, StartOperationOptions options)
      throws OperationException {
    Objects.requireNonNull(operation, "operation");
    Objects.requireNonNull(resultType, "resultType");
    Objects.requireNonNull(options, "options");
    io.nexusrpc.client.transport.StartOperationOptions.Builder transportOptions =
        io.nexusrpc.client.transport.StartOperationOptions.newBuilder()
            .setRequestId(options.getRequestId())
            .setInboundLinks(options.getInboundLinks())
            .setCallbackURL(options.getCallbackURL());

    options.getHeaders().forEach(transportOptions::putHeader);

    options.getCallbackHeaders().forEach(transportOptions::putCallbackHeader);
    io.nexusrpc.client.transport.StartOperationResponse response =
        transport.startOperation(
            operation, serviceDefinition.getName(), input, transportOptions.build());
    if (response.getAsyncOperationToken() == null) {
      // If the operation is synchronous, return the result directly
      return new StartOperationResponse.Sync<>(
          (R) serializer.deserialize(response.getSyncResult(), resultType), response.getLinks());
    }
    // If the operation is asynchronous, return a handle to the operation
    return new StartOperationResponse.Async<>(
        new OperationHandle<>(
            operation,
            serviceDefinition.getName(),
            response.getAsyncOperationToken(),
            resultType,
            serializer,
            transport),
        response.getLinks());
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   */
  public <U, R> CompletableFuture<StartOperationResponse<R>> startOperationAsync(
      BiFunction<T, U, R> operation, U input) {
    return startOperationAsync(operation, input, StartOperationOptions.newBuilder().build());
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation method to start, represented as a BiFunction.
   * @param input The input to the operation.
   * @param options for start operations
   */
  public <U, R> CompletableFuture<StartOperationResponse<R>> startOperationAsync(
      BiFunction<T, U, R> operation, U input, StartOperationOptions options) {
    OperationDefinition operationDefinition = extractOperationDefinition(serviceClass, operation);
    return startOperationAsync(
        operationDefinition.getName(), operationDefinition.getOutputType(), input, options);
  }

  /**
   * Starts an operation on the Nexus service with the provided input.
   *
   * @param operation The operation name to start.
   * @param input The input to the operation.
   * @param options for start operations
   */
  public <U, R> CompletableFuture<StartOperationResponse<R>> startOperationAsync(
      String operation, Type resultType, U input, StartOperationOptions options) {
    Objects.requireNonNull(operation, "operation");
    Objects.requireNonNull(resultType, "resultType");
    Objects.requireNonNull(options, "options");
    io.nexusrpc.client.transport.StartOperationOptions.Builder transportOptions =
        io.nexusrpc.client.transport.StartOperationOptions.newBuilder()
            .setRequestId(options.getRequestId())
            .setInboundLinks(options.getInboundLinks())
            .setCallbackURL(options.getCallbackURL());

    options.getHeaders().forEach(transportOptions::putHeader);

    options.getCallbackHeaders().forEach(transportOptions::putCallbackHeader);
    CompletableFuture<io.nexusrpc.client.transport.StartOperationResponse> response =
        transport.startOperationAsync(
            operation, serviceDefinition.getName(), input, transportOptions.build());
    return response.thenApply(
        r -> {
          if (r.getAsyncOperationToken() == null) {
            // If the operation is synchronous, return the result directly
            return new StartOperationResponse.Sync<>(
                (R) serializer.deserialize(r.getSyncResult(), resultType), r.getLinks());
          }
          // If the operation is asynchronous, return a handle to the operation
          return new StartOperationResponse.Async<>(
              new OperationHandle<>(
                  operation,
                  serviceDefinition.getName(),
                  r.getAsyncOperationToken(),
                  resultType,
                  serializer,
                  transport),
              r.getLinks());
        });
  }

  /**
   * Gets a handle to an asynchronous operation.
   *
   * <p>Note: This does not perform validation that the token is valid.
   *
   * @param operation name of the operation.
   * @param token operation token.
   */
  public OperationHandle<Object> newHandle(String operation, String token) {
    Objects.requireNonNull(operation, "operation");
    Objects.requireNonNull(token, "token");
    return new OperationHandle<>(
        operation, serviceDefinition.getName(), token, Object.class, serializer, transport);
  }

  /**
   * Gets a handle to an asynchronous operation.
   *
   * <p>Note: This does not perform validation that the token is valid.
   *
   * @param operation operation method.
   * @param token operation token.
   */
  public <U, R> OperationHandle<R> newHandle(BiFunction<T, U, R> operation, String token) {
    Objects.requireNonNull(operation, "operation");
    Objects.requireNonNull(token, "token");
    OperationDefinition operationDefinition = extractOperationDefinition(serviceClass, operation);
    return new OperationHandle<>(
        operationDefinition.getName(),
        serviceDefinition.getName(),
        token,
        operationDefinition.getOutputType(),
        serializer,
        transport);
  }

  static <T> OperationDefinition extractOperationDefinition(
      Class<T> serviceClass, BiFunction<T, ?, ?> operation) {
    AtomicReference<OperationDefinition> operationDefinition = new AtomicReference<>();
    //noinspection unchecked
    T p =
        (T)
            Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[] {serviceClass},
                (proxy, method, args) -> {
                  operationDefinition.set(OperationDefinition.fromMethod(method));
                  return null; // No actual method invocation, just capturing the operation name
                });
    operation.apply(p, null);
    if (operationDefinition.get() == null) {
      throw new IllegalArgumentException("Operation definition not found");
    }
    return operationDefinition.get();
  }
}
