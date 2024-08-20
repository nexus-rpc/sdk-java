package io.nexusrpc.handler;

import io.nexusrpc.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

  private ServiceHandler(Map<String, ServiceImplInstance> instances, Serializer serializer) {
    this.instances = instances;
    this.serializer = serializer;
  }

  /** Instances, by service name. */
  public Map<String, ServiceImplInstance> getInstances() {
    return instances;
  }

  /** Serializer used for input/output. */
  public Serializer getSerializer() {
    return serializer;
  }

  @Override
  @SuppressWarnings("unchecked")
  public OperationStartResult<HandlerResultContent> startOperation(
      OperationContext context, OperationStartDetails details, InputStream param)
      throws UnrecognizedOperationException, OperationUnsuccessfulException {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationDefinition definition =
        instance.getDefinition().getOperations().get(context.getOperation());

    Object input;
    try {
      // Collect entire input stream as byte array. This is unfortunately the best Java-8-safe
      // approach.
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[1024];
      while ((nRead = param.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      // Deserialize to expected input type
      Serializer.Content.Builder contentBuilder = Serializer.Content.newBuilder();
      contentBuilder.setData(buffer.toByteArray());
      contentBuilder.getHeaders().putAll(context.getHeaders());
      input = serializer.deserialize(contentBuilder.build(), definition.getInputType());
    } catch (Exception e) {
      throw new RuntimeException("Failed deserializing input", e);
    }

    // Invoke handler
    OperationStartResult<?> result = handler.start(context, details, input);

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
      throws UnrecognizedOperationException,
          OperationNotFoundException,
          OperationStillRunningException,
          OperationUnsuccessfulException {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    Object result = handler.fetchResult(context, details);
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
      OperationContext context, OperationFetchInfoDetails details)
      throws UnrecognizedOperationException, OperationNotFoundException {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    return handler.fetchInfo(context, details);
  }

  @Override
  public void cancelOperation(OperationContext context, OperationCancelDetails details)
      throws UnrecognizedOperationException, OperationNotFoundException {
    ServiceImplInstance instance = instances.get(context.getService());
    if (instance == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    OperationHandler<Object, Object> handler =
        instance.getOperationHandlers().get(context.getOperation());
    if (handler == null) {
      throw new UnrecognizedOperationException(context.getService(), context.getOperation());
    }
    handler.cancel(context, details);
  }

  /** Builder for operation start details. */
  public static class Builder {
    private final List<ServiceImplInstance> instances;
    private @Nullable Serializer serializer;

    private Builder() {
      this.instances = new ArrayList<>();
    }

    private Builder(ServiceHandler handler) {
      // Order does not matter for instances except in case of validation
      // errors.
      instances = new ArrayList<>(handler.instances.values());
      serializer = handler.serializer;
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
      return new ServiceHandler(Collections.unmodifiableMap(instancesByName), serializer);
    }
  }
}