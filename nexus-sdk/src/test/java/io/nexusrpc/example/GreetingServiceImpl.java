package io.nexusrpc.example;

import io.nexusrpc.*;
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
    return OperationHandler.sync((ctx, name) -> "Hello, " + name + "!");
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
    public StartResult<String> start(StartContext ctx, @Nullable String name) {
      // For purposes of this sample, let's say if name starts with sync we do a
      // sync return. This demonstrates that at runtime the decision can be made.
      if (name == null) {
        name = "<unknown>";
      }
      if (name.startsWith("sync-")) {
        return StartResult.sync("Hello, " + name + "!");
      }
      if (ctx.getCallbackUrl() != null) {
        throw new IllegalArgumentException("This service does not support callbacks");
      }
      String id = UUID.randomUUID().toString();
      operations.put(id, apiClient.createGreeting(name));
      return StartResult.async(id);
    }

    @Override
    public String fetchResult(FetchResultContext ctx)
        throws OperationNotFoundException, OperationStillRunningException {
      Future<String> operation = getOperation(ctx.getOperationId());
      try {
        // When timeout missing, be done or fail
        if (ctx.getTimeout() == null) {
          if (!operation.isDone()) {
            throw new OperationStillRunningException();
          }
          return operation.get();
        }
        // User willing to wait
        try {
          return operation.get(ctx.getTimeout().toMillis(), TimeUnit.MILLISECONDS);
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
    public OperationInfo fetchInfo(FetchInfoContext ctx) throws OperationNotFoundException {
      Future<String> operation = getOperation(ctx.getOperationId());
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
      return OperationInfo.newBuilder().setId(ctx.getOperationId()).setState(state).build();
    }

    @Override
    public void cancel(CancelContext ctx) throws OperationNotFoundException {
      getOperation(ctx.getOperationId()).cancel(true);
    }

    private Future<String> getOperation(String id) throws OperationNotFoundException {
      Future<String> operation = operations.get(id);
      if (operation == null) {
        throw new OperationNotFoundException();
      }
      return operation;
    }
  }
}
