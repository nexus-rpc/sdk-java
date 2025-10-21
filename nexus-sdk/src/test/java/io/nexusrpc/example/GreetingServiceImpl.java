package io.nexusrpc.example;

import io.nexusrpc.*;
import io.nexusrpc.handler.*;
import java.net.URI;
import java.net.URISyntaxException;
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
          URI url = new URI("http://somepath?k=v");
          context.addLinks(Link.newBuilder().setUri(url).setType("com.example.MyResource").build());
          return OperationStartResult.<String>newAsyncBuilder(id).build();
        } catch (URISyntaxException e) {
          throw new RuntimeException(e);
        }
      }
      return OperationStartResult.async(id);
    }

    @Override
    public void cancel(OperationContext context, OperationCancelDetails details) {
      getOperation(details.getOperationToken()).cancel(true);
    }

    private Future<String> getOperation(String id) {
      Future<String> operation = operations.get(id);
      if (operation == null) {
        throw new HandlerException(
            HandlerException.ErrorType.NOT_FOUND, "Operation not found for ID: " + id);
      }
      return operation;
    }
  }
}
