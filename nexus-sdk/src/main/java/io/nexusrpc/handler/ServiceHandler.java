package io.nexusrpc.handler;

import io.nexusrpc.*;
import java.util.*;
import org.jspecify.annotations.Nullable;

/** Handler that delegates to service implementations. */
public class ServiceHandler implements Handler {
  /** Create a builder for a service handler. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder for service handler from an existing service handler. */
  public static Builder newBuilder(ServiceHandler handler) {
    return new Builder(handler);
  }

  private final Map<String, ServiceImplInstance> instances;
  private final Serializer serializer;
  private final List<OperationMiddleware> middlewares;

  private ServiceHandler(
      Map<String, ServiceImplInstance> instances,
      Serializer serializer,
      List<OperationMiddleware> middlewares) {
    this.instances = instances;
    this.serializer = serializer;
    this.middlewares = middlewares;
  }

  /** Instances, by service name. */
  public Map<String, ServiceImplInstance> getInstances() {
    return instances;
  }

  /** Serializer used for input/output. */
  public Serializer getSerializer() {
    return serializer;
  }

  private OperationHandler<Object, Object> getOperationHandler(
      OperationHandler<Object, Object> rootHandler) {
    OperationHandler<Object, Object> handler = rootHandler;
    ListIterator<OperationMiddleware> li = middlewares.listIterator(middlewares.size());
    while (li.hasPrevious()) {
      handler = li.previous().intercept(handler);
    }
    return handler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public OperationStartResult<HandlerResultContent> startOperation(
      OperationContext context, OperationStartDetails details, HandlerInputContent input)
      throws OperationUnsuccessfulException {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationDefinition definition =
        instance.getDefinition().getOperations().get(context.getOperation());

    Object inputObject;
    try {
      // Deserialize to expected input type
      Serializer.Content.Builder contentBuilder = Serializer.Content.newBuilder();
      contentBuilder.setData(input.consumeBytes());
      contentBuilder.getHeaders().putAll(input.getHeaders());
      inputObject = serializer.deserialize(contentBuilder.build(), definition.getInputType());
    } catch (Exception e) {
      throw new RuntimeException("Failed deserializing input", e);
    }

    // Invoke handler
    OperationStartResult<?> result =
        getOperationHandler(handler).start(context, details, inputObject);

    // If the result is an async result we can just return, but if it's a sync result we need to
    // serialize back out to bytes
    if (!result.isSync()) {
      return (OperationStartResult<HandlerResultContent>) result;
    }
    // Convert to result content
    return OperationStartResult.sync(resultToContent(result.getSyncResult()));
  }

  @Override
  public HandlerResultContent fetchOperationResult(
      OperationContext context, OperationFetchResultDetails details)
      throws OperationStillRunningException, OperationUnsuccessfulException {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    Object result = getOperationHandler(handler).fetchResult(context, details);
    return resultToContent(result);
  }

  private HandlerResultContent resultToContent(Object result) {
    try {
      Serializer.Content output = serializer.serialize(result);
      HandlerResultContent.Builder contentBuilder = HandlerResultContent.newBuilder();
      contentBuilder.setData(output.getData());
      contentBuilder.getHeaders().putAll(output.getHeaders());
      return contentBuilder.build();
    } catch (Exception e) {
      throw new RuntimeException("Failed serializing result", e);
    }
  }

  @Override
  public OperationInfo fetchOperationInfo(
      OperationContext context, OperationFetchInfoDetails details) {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    return getOperationHandler(handler).fetchInfo(context, details);
  }

  @Override
  public void cancelOperation(OperationContext context, OperationCancelDetails details) {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw newUnrecognizedOperationException(context.getService(), context.getOperation());
    }
    getOperationHandler(handler).cancel(context, details);
  }

  private static OperationHandlerException newUnrecognizedOperationException(
      String service, String operation) {
    return new OperationHandlerException(
        OperationHandlerException.ErrorType.NOT_FOUND,
        "Unrecognized service " + service + " or operation " + operation);
  }

  /** Builder for operation start details. */
  public static class Builder {
    private final List<ServiceImplInstance> instances;
    private @Nullable Serializer serializer;
    private List<OperationMiddleware> middlewares;

    private Builder() {
      this.instances = new ArrayList<>();
      this.middlewares = new ArrayList<>();
    }

    private Builder(ServiceHandler handler) {
      // Order does not matter for instances except in case of validation
      // errors.
      instances = new ArrayList<>(handler.instances.values());
      serializer = handler.serializer;
      middlewares = new ArrayList<>(handler.middlewares);
    }

    /** Get instances to mutate. */
    public List<ServiceImplInstance> getInstances() {
      return instances;
    }

    /** Add a service instance. */
    public Builder addInstance(ServiceImplInstance instance) {
      instances.add(instance);
      return this;
    }

    /** Serializer. Required. */
    public Builder setSerializer(Serializer serializer) {
      this.serializer = serializer;
      return this;
    }

    /** Add a middleware to the Service Handler. */
    public Builder addOperationMiddleware(OperationMiddleware middleware) {
      middlewares.add(middleware);
      return this;
    }

    /** Get links. */
    public List<OperationMiddleware> getMiddlewares() {
      return middlewares;
    }

    /** Build the handler. */
    public ServiceHandler build() {
      if (instances.isEmpty()) {
        throw new IllegalStateException("No service instances defined");
      }
      Objects.requireNonNull(serializer, "Serializer required");
      Map<String, ServiceImplInstance> instancesByName = new HashMap<>(instances.size());
      for (ServiceImplInstance instance : instances) {
        if (instancesByName.containsKey(instance.getDefinition().getName())) {
          throw new IllegalStateException(
              "Multiple instances registered for service name '"
                  + instance.getDefinition().getName()
                  + "'");
        }
        instancesByName.put(instance.getDefinition().getName(), instance);
      }
      return new ServiceHandler(
          Collections.unmodifiableMap(instancesByName), serializer, middlewares);
    }
  }
}
