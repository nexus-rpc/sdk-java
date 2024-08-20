package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.*;

import io.nexusrpc.*;
import io.nexusrpc.example.ApiClient;
import io.nexusrpc.example.GreetingServiceImpl;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class ServiceHandlerTest {
  @Test
  void simpleGreetingService()
      throws UnrecognizedOperationException,
          OperationUnsuccessfulException,
          OperationNotFoundException,
          OperationStillRunningException {
    // Create API client
    AtomicReference<ApiClient> apiClientInternal = new AtomicReference<>();
    ApiClient apiClient = name -> apiClientInternal.get().createGreeting(name);

    // Create service handler
    ServiceHandler handler =
        ServiceHandler.newBuilder()
            .setSerializer(new StringOnlySerializer())
            .addInstance(ServiceImplInstance.fromInstance(new GreetingServiceImpl(apiClient)))
            .build();

    // Call synchronous form
    OperationStartResult<HandlerResultContent> result =
        handler.startOperation(
            OperationContext.newBuilder()
                .setService("GreetingService")
                .setOperation("sayHello1")
                .build(),
            OperationStartDetails.newBuilder().setRequestId("request-id-1").build(),
            new ByteArrayInputStream("SomeUser".getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "Hello, SomeUser!",
        new String(
            Objects.requireNonNull(Objects.requireNonNull(result.getSyncResult()).getDataBytes()),
            StandardCharsets.UTF_8));

    // Call handler form with sync-prefixed name which uses synchronous handling
    result =
        handler.startOperation(
            OperationContext.newBuilder()
                .setService("GreetingService")
                .setOperation("sayHello2")
                .build(),
            OperationStartDetails.newBuilder().setRequestId("request-id-2").build(),
            new ByteArrayInputStream("sync-SomeUser".getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "Hello, sync-SomeUser!",
        new String(
            Objects.requireNonNull(Objects.requireNonNull(result.getSyncResult()).getDataBytes()),
            StandardCharsets.UTF_8));

    // Call handler form with regular name which uses asynchronous handling. First, have the API
    // client wait.
    AtomicReference<CompletableFuture<String>> pendingFuture = new AtomicReference<>();
    apiClientInternal.set(
        name -> {
          assertEquals("SomeUser", name);
          pendingFuture.set(new CompletableFuture<>());
          return pendingFuture.get();
        });
    // Now call
    result =
        handler.startOperation(
            OperationContext.newBuilder()
                .setService("GreetingService")
                .setOperation("sayHello2")
                .build(),
            OperationStartDetails.newBuilder().setRequestId("request-id-3").build(),
            new ByteArrayInputStream("SomeUser".getBytes(StandardCharsets.UTF_8)));
    String operationId = Objects.requireNonNull(result.getAsyncOperationId());
    // Confirm future is waiting and info says it's running
    OperationInfo info =
        handler.fetchOperationInfo(
            OperationContext.newBuilder()
                .setService("GreetingService")
                .setOperation("sayHello2")
                .build(),
            OperationFetchInfoDetails.newBuilder().setOperationId(operationId).build());
    assertEquals(OperationState.RUNNING, info.getState());
    // Resolve future and confirm succeeded
    Objects.requireNonNull(pendingFuture.get()).complete("Hello from API, SomeUser!");
    info =
        handler.fetchOperationInfo(
            OperationContext.newBuilder()
                .setService("GreetingService")
                .setOperation("sayHello2")
                .build(),
            OperationFetchInfoDetails.newBuilder().setOperationId(operationId).build());
    assertEquals(OperationState.SUCCEEDED, info.getState());
    // Check result
    HandlerResultContent content =
        handler.fetchOperationResult(
            OperationContext.newBuilder()
                .setService("GreetingService")
                .setOperation("sayHello2")
                .build(),
            OperationFetchResultDetails.newBuilder().setOperationId(operationId).build());
    assertEquals(
        "Hello from API, SomeUser!",
        new String(Objects.requireNonNull(content.getDataBytes()), StandardCharsets.UTF_8));
  }
}