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
      throws OperationUnsuccessfulException, OperationStillRunningException {
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
            newGreetingServiceContext("sayHello1"),
            OperationStartDetails.newBuilder().setRequestId("request-id-1").build(),
            newSimpleInputContent("SomeUser"));
    assertEquals(
        "Hello, SomeUser!",
        new String(
            Objects.requireNonNull(Objects.requireNonNull(result.getSyncResult()).getDataBytes()),
            StandardCharsets.UTF_8));

    // Call handler form with sync-prefixed name which uses synchronous handling
    result =
        handler.startOperation(
            newGreetingServiceContext("sayHello2"),
            OperationStartDetails.newBuilder().setRequestId("request-id-2").build(),
            newSimpleInputContent("sync-SomeUser"));
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
            newGreetingServiceContext("sayHello2"),
            OperationStartDetails.newBuilder().setRequestId("request-id-3").build(),
            newSimpleInputContent("SomeUser"));
    String operationId = Objects.requireNonNull(result.getAsyncOperationId());
    // Confirm future is waiting and info says it's running
    OperationInfo info =
        handler.fetchOperationInfo(
            newGreetingServiceContext("sayHello2"),
            OperationFetchInfoDetails.newBuilder().setOperationId(operationId).build());
    assertEquals(OperationState.RUNNING, info.getState());
    // Resolve future and confirm succeeded
    Objects.requireNonNull(pendingFuture.get()).complete("Hello from API, SomeUser!");
    info =
        handler.fetchOperationInfo(
            newGreetingServiceContext("sayHello2"),
            OperationFetchInfoDetails.newBuilder().setOperationId(operationId).build());
    assertEquals(OperationState.SUCCEEDED, info.getState());
    // Check result
    HandlerResultContent content =
        handler.fetchOperationResult(
            newGreetingServiceContext("sayHello2"),
            OperationFetchResultDetails.newBuilder().setOperationId(operationId).build());
    assertEquals(
        "Hello from API, SomeUser!",
        new String(Objects.requireNonNull(content.getDataBytes()), StandardCharsets.UTF_8));
  }

  private static OperationContext newGreetingServiceContext(String operation) {
    return OperationContext.newBuilder()
        .setService("GreetingService")
        .setOperation(operation)
        .build();
  }

  private static HandlerInputContent newSimpleInputContent(String value) {
    return HandlerInputContent.newBuilder()
        .setDataStream(new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)))
        .build();
  }
}
