package io.nexusrpc.example;

import io.nexusrpc.*;
import io.nexusrpc.handler.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.*;
import org.jspecify.annotations.Nullable;

@ServiceImpl(service = GreetingService.class)
public class GreetingServiceImpl {
  private final ApiClient apiClient;

  public GreetingServiceImpl(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  @OperationImpl
  public OperationHandler<String, String> sayHello1() {
    // Implemented inline
    return OperationHandler.sync((ctx, details, name) -> "Hello, " + name + "!");
  }

  @OperationImpl
  public OperationHandler<String, String> sayHello2() {
    // Implemented via handler
    return new SayHello2Handler();
  }

  // Naive example showing async operations tracked in-memory
  private class SayHello2Handler implements OperationHandler<String, String> {
    private final ConcurrentMap<String, Future<String>> operations = new ConcurrentHashMap<>();

    @Override
    public OperationStartResult<String> start(
        OperationContext context, OperationStartDetails details, @Nullable String name) {
      // For purposes of this sample, let's say if name starts with sync we do a
      // sync return. This demonstrates that at runtime the decision can be made.
      if (name == null) {
        name = "<unknown>";
      }
      if (name.startsWith("sync-")) {
        return OperationStartResult.sync("Hello, " + name + "!");
      }
      if (details.getCallbackUrl() != null) {
        throw new IllegalArgumentException("This service does not support callbacks");
      }
      String id = UUID.randomUUID().toString();
      operations.put(id, apiClient.createGreeting(name));
      if (name.endsWith("link")) {
        try {
          URL url = new URL("http://somepath?k=v");
          return OperationStartResult.<String>newBuilder()
              .setAsyncOperationId(id)
              .addLink(Link.newBuilder().setUrl(url).setType("com.example.MyResource").build())
              .build();
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }
      }
      return OperationStartResult.async(id);
    }

    @Override
    public String fetchResult(OperationContext context, OperationFetchResultDetails details)
        throws OperationStillRunningException {
      Future<String> operation = getOperation(details.getOperationId());
      try {
        // When timeout missing, be done or fail
        if (details.getTimeout() == null) {
          if (!operation.isDone()) {
            throw new OperationStillRunningException();
          }
          return operation.get();
        }
        // User willing to wait
        try {
          return operation.get(details.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
          throw new OperationStillRunningException();
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (ExecutionException e) {
        if (e.getCause() instanceof RuntimeException) {
          throw (RuntimeException) e.getCause();
        }
        throw new RuntimeException(e.getCause());
      }
    }

    @Override
    public OperationInfo fetchInfo(OperationContext context, OperationFetchInfoDetails details) {
      Future<String> operation = getOperation(details.getOperationId());
      OperationState state;
      if (operation.isCancelled()) {
        state = OperationState.CANCELLED;
      } else if (!operation.isDone()) {
        state = OperationState.RUNNING;
      } else {
        try {
          operation.get();
          state = OperationState.SUCCEEDED;
        } catch (Exception e) {
          state = OperationState.FAILED;
        }
      }
      return OperationInfo.newBuilder().setId(details.getOperationId()).setState(state).build();
    }

    @Override
    public void cancel(OperationContext context, OperationCancelDetails details) {
      getOperation(details.getOperationId()).cancel(true);
    }

    private Future<String> getOperation(String id) {
      Future<String> operation = operations.get(id);
      if (operation == null) {
        throw new OperationHandlerException(
            OperationHandlerException.ErrorType.NOT_FOUND, "Operation not found for ID: " + id);
      }
      return operation;
    }
  }
}
