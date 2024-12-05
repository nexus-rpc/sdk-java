package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.*;

import io.nexusrpc.*;
import io.nexusrpc.example.ApiClient;
import io.nexusrpc.example.GreetingServiceImpl;
import io.nexusrpc.example.TestServices;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class ServiceHandlerTest {
  @ServiceImpl(service = TestServices.GenericService.class)
  public class GenericServiceMissingOperationImpl {}

  @ServiceImpl(service = TestServices.GenericService.class)
  public class GenericServiceMismatchOutputArgumentImpl {
    @OperationImpl
    public OperationHandler<String, Integer> operation() {
      return OperationHandler.sync((ctx, details, name) -> 0);
    }
  }

  @ServiceImpl(service = TestServices.GenericService.class)
  public class GenericServiceNotReturningAnOperationHandleImpl {
    @OperationImpl
    public Integer operation() {
      return 0;
    }
  }

  @ServiceImpl(service = TestServices.GenericService.class)
  public class GenericServiceMismatchInputArgumentImpl {
    @OperationImpl
    public OperationHandler<Integer, String> operation() {
      return OperationHandler.sync((ctx, details, name) -> "");
    }
  }

  @Test
  void serviceImplMissingOperation() {
    assertThrows(
        RuntimeException.class,
        () -> ServiceImplInstance.fromInstance(new GenericServiceMissingOperationImpl()));
  }

  @Test
  void serviceImplNotReturningAnOperationHandle() {
    assertThrows(
        RuntimeException.class,
        () ->
            ServiceImplInstance.fromInstance(
                new GenericServiceNotReturningAnOperationHandleImpl()));
  }

  @Test
  void serviceImplMismatchOutputArgument() {
    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () -> ServiceImplInstance.fromInstance(new GenericServiceMismatchOutputArgumentImpl()));
    assertTrue(
        ex.getCause()
            .getMessage()
            .contains(
                "OperationHandler output type mismatch expected java.lang.String but got java.lang.Integer"));
  }

  @Test
  void serviceImplMismatchInputArgument() {
    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () -> ServiceImplInstance.fromInstance(new GenericServiceMismatchInputArgumentImpl()));
    assertTrue(
        ex.getCause()
            .getMessage()
            .contains(
                "OperationHandler input type mismatch expected java.lang.String but got java.lang.Integer"));
  }

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
          assertTrue(name.startsWith("SomeUser"));
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
    // Test an async operation with a link
    OperationStartResult<HandlerResultContent> resultWithLink =
        handler.startOperation(
            newGreetingServiceContext("sayHello2"),
            OperationStartDetails.newBuilder().setRequestId("request-id-4").build(),
            newSimpleInputContent("SomeUser-link"));
    Objects.requireNonNull(resultWithLink.getAsyncOperationId());
    List<Link> links = Objects.requireNonNull(resultWithLink.getLinks());
    assertEquals(1, links.size());
    assertEquals("http://somepath?k=v", links.get(0).getUri().toString());
    assertEquals("com.example.MyResource", links.get(0).getType());
  }

  @Test
  void serviceWithMiddleware() throws OperationUnsuccessfulException {
    // Create API client
    AtomicReference<ApiClient> apiClientInternal = new AtomicReference<>();
    ApiClient apiClient = name -> apiClientInternal.get().createGreeting(name);

    String authToken = "auth-token";

    LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
    // Create service handler
    ServiceHandler handler =
        ServiceHandler.newBuilder()
            .setSerializer(new StringOnlySerializer())
            .addInstance(ServiceImplInstance.fromInstance(new GreetingServiceImpl(apiClient)))
            .addOperationMiddleware(new AuthInterceptor(authToken))
            .addOperationMiddleware(loggingInterceptor)
            .build();

    // Call synchronous form with valid auth token
    OperationStartResult<HandlerResultContent> result =
        handler.startOperation(
            OperationContext.newBuilder()
                .setService("GreetingService")
                .setOperation("sayHello1")
                .putHeader(AuthInterceptor.AUTH_HEADER, authToken)
                .build(),
            OperationStartDetails.newBuilder().setRequestId("request-id-1").build(),
            newSimpleInputContent("SomeUser"));
    assertEquals(
        "Hello, SomeUser!",
        new String(
            Objects.requireNonNull(Objects.requireNonNull(result.getSyncResult()).getDataBytes()),
            StandardCharsets.UTF_8));
    // Call synchronous form with invalid auth token
    assertThrows(
        OperationHandlerException.class,
        () ->
            handler.startOperation(
                OperationContext.newBuilder()
                    .setService("GreetingService")
                    .setOperation("sayHello1")
                    .build(),
                OperationStartDetails.newBuilder().setRequestId("request-id-2").build(),
                newSimpleInputContent("SomeUser")));
    // Verify logging interceptor was called and only saw the first operation
    assertEquals(loggingInterceptor.getOperations(), Collections.singletonList("sayHello1"));
  }

  private static class LoggingInterceptor implements OperationMiddleware {
    private List<String> operations = Collections.synchronizedList(new ArrayList<>());

    public List<String> getOperations() {
      return operations;
    }

    @Override
    public OperationHandler<Object, Object> intercept(
        OperationContext context, OperationHandler<Object, Object> next) {
      return new LoggingOperationMiddleware(operations, next);
    }

    private static class LoggingOperationMiddleware implements OperationHandler<Object, Object> {
      private final OperationHandler<Object, Object> next;
      private final List<String> operations;

      private LoggingOperationMiddleware(
          List<String> operations, OperationHandler<Object, Object> next) {
        this.operations = operations;
        this.next = next;
      }

      @Override
      public OperationStartResult<Object> start(
          OperationContext context, OperationStartDetails details, @Nullable Object param)
          throws OperationUnsuccessfulException, OperationHandlerException {
        operations.add(context.getOperation());
        return next.start(context, details, param);
      }

      @Override
      public @Nullable Object fetchResult(
          OperationContext context, OperationFetchResultDetails details)
          throws OperationStillRunningException,
              OperationUnsuccessfulException,
              OperationHandlerException {
        operations.add(context.getOperation());
        return next.fetchResult(context, details);
      }

      @Override
      public OperationInfo fetchInfo(OperationContext context, OperationFetchInfoDetails details)
          throws OperationHandlerException {
        operations.add(context.getOperation());
        return next.fetchInfo(context, details);
      }

      @Override
      public void cancel(OperationContext context, OperationCancelDetails details)
          throws OperationHandlerException {
        operations.add(context.getOperation());
        next.cancel(context, details);
      }
    }
  }

  private static class AuthInterceptor implements OperationMiddleware {
    public static final String AUTH_HEADER = "Authorization";
    private final String authToken;

    private AuthInterceptor(String authToken) {
      this.authToken = authToken;
    }

    @Override
    public OperationHandler<Object, Object> intercept(
        OperationContext context, OperationHandler<Object, Object> next) {
      if (authToken != context.getHeaders().get(AUTH_HEADER)) {
        throw new OperationHandlerException(
            OperationHandlerException.ErrorType.UNAUTHORIZED, "Unauthorized");
      }
      return next;
    }
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
