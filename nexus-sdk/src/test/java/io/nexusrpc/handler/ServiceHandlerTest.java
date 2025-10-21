package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.*;

import io.nexusrpc.*;
import io.nexusrpc.example.ApiClient;
import io.nexusrpc.example.GreetingServiceImpl;
import io.nexusrpc.example.TestServices;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

  @ServiceImpl(service = TestServices.VoidService.class)
  public class VoidServiceImpl {
    @OperationImpl
    public OperationHandler<Void, Void> operation() {
      return OperationHandler.sync((ctx, details, name) -> null);
    }
  }

  @ServiceImpl(service = TestServices.IntService.class)
  public class IntServiceImpl {
    @OperationImpl
    public OperationHandler<Integer, Integer> operation() {
      return OperationHandler.sync((ctx, details, input) -> 0);
    }
  }

  @ServiceImpl(service = TestServices.IntegerService.class)
  public class IntegerServiceImpl {
    @OperationImpl
    public OperationHandler<Integer, Integer> operation() {
      return OperationHandler.sync((ctx, details, input) -> 0);
    }
  }

  @ServiceImpl(service = TestServices.GenericParameterService.class)
  public class genericParameterServiceImpl {
    @OperationImpl
    public OperationHandler<Map<String, List<String>>, Map<String, List<String>>> operation() {
      return OperationHandler.sync((ctx, details, input) -> null);
    }
  }

  @ServiceImpl(service = TestServices.GenericParameterService.class)
  public class MismatchGenericParameterServiceImpl {
    @OperationImpl
    public OperationHandler<Map<String, List<Integer>>, Map<String, List<Integer>>> operation() {
      return OperationHandler.sync((ctx, details, input) -> null);
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
  void genericServiceImplMismatchInputArgument() {
    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () -> ServiceImplInstance.fromInstance(new MismatchGenericParameterServiceImpl()));
    assertTrue(
        ex.getCause()
            .getMessage()
            .contains(
                "OperationHandler input type mismatch expected java.util.Map<java.lang.String, java.util.List<java.lang.String>> but got java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>"));
  }

  @Test
  void voidService() {
    ServiceImplInstance serviceImpl = ServiceImplInstance.fromInstance(new VoidServiceImpl());
    assertEquals(1, serviceImpl.getOperationHandlers().size());
  }

  @Test
  void intService() {
    ServiceImplInstance serviceImpl = ServiceImplInstance.fromInstance(new IntServiceImpl());
    assertEquals(1, serviceImpl.getOperationHandlers().size());
  }

  @Test
  void integerService() {
    ServiceImplInstance serviceImpl = ServiceImplInstance.fromInstance(new IntegerServiceImpl());
    assertEquals(1, serviceImpl.getOperationHandlers().size());
  }

  @Test
  void genericParameterService() {
    ServiceImplInstance serviceImpl =
        ServiceImplInstance.fromInstance(new genericParameterServiceImpl());
    assertEquals(1, serviceImpl.getOperationHandlers().size());
  }

  @Test
  void simpleGreetingService() throws OperationException {
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
    String operationToken = Objects.requireNonNull(result.getAsyncOperationToken());
    // Test an async operation with a link
    OperationContext octx = newGreetingServiceContext("sayHello2");
    OperationStartResult<HandlerResultContent> resultWithLink =
        handler.startOperation(
            octx,
            OperationStartDetails.newBuilder().setRequestId("request-id-4").build(),
            newSimpleInputContent("SomeUser-link"));
    Objects.requireNonNull(resultWithLink.getAsyncOperationToken());
    List<Link> links = Objects.requireNonNull(octx.getLinks());
    assertEquals(1, links.size());
    assertEquals("http://somepath?k=v", links.get(0).getUri().toString());
    assertEquals("com.example.MyResource", links.get(0).getType());
  }

  @Test
  void serviceWithMiddleware() throws OperationException {
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
        HandlerException.class,
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
          throws OperationException, HandlerException {
        operations.add(context.getOperation());
        return next.start(context, details, param);
      }

      @Override
      public void cancel(OperationContext context, OperationCancelDetails details)
          throws HandlerException {
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
        throw new HandlerException(HandlerException.ErrorType.UNAUTHORIZED, "Unauthorized");
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
