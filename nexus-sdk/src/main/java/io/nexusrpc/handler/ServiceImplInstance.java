package io.nexusrpc.handler;

import io.nexusrpc.OperationDefinition;
import io.nexusrpc.ServiceDefinition;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import org.jspecify.annotations.Nullable;

/** Instance of a {@link ServiceImpl} annotated class. */
public class ServiceImplInstance {
  /**
   * Create a service instance from the given object instance. The object must an instance of a
   * class annotated with {@link ServiceImpl} and must pass all validation for the required
   * operations.
   */
  public static ServiceImplInstance fromInstance(Object instance) {
    // Expect the annotation on the class of the instance itself, do not expect it to be inherited
    // and go searching through superclasses
    ServiceImpl serviceImpl = instance.getClass().getDeclaredAnnotation(ServiceImpl.class);
    if (serviceImpl == null) {
      throw new IllegalArgumentException("Missing @ServiceImpl annotation");
    } else if (serviceImpl.service() == null) {
      throw new IllegalArgumentException("@ServiceImpl annotation missing service class");
    }
    ServiceDefinition serviceDefinition;
    try {
      serviceDefinition = ServiceDefinition.fromClass(serviceImpl.service());
    } catch (Exception e) {
      throw new RuntimeException("Failed loading @ServiceImpl class " + serviceImpl.service(), e);
    }

    // Collect all methods, then walk them looking for operation handlers
    List<Method> methods = new ArrayList<>();
    collectClassMethods(instance.getClass(), methods);
    Builder builder = newBuilder().setDefinition(serviceDefinition);
    for (Method method : methods) {
      OperationImpl operationImpl = method.getDeclaredAnnotation(OperationImpl.class);
      if (operationImpl == null) {
        continue;
      }
      try {
        addOperationHandler(builder, serviceDefinition, instance, method);
      } catch (Exception e) {
        throw new RuntimeException(
            "Failed obtaining operation handler from " + method.getName(), e);
      }
    }

    // Do build which will fail if there are any handlers missing for the definitions or any
    // handlers without a definition
    return builder.build();
  }

  private static void addOperationHandler(
      Builder builder, ServiceDefinition serviceDefinition, Object instance, Method method) {
    // Basic validation
    if (method.getParameterCount() > 0) {
      throw new IllegalArgumentException("Cannot have any parameters");
    } else if (method.getTypeParameters().length > 0) {
      throw new IllegalArgumentException("Cannot be generic");
    } else if (method.getExceptionTypes().length > 0) {
      throw new IllegalArgumentException("Cannot have throws clause");
    } else if (!Modifier.isPublic(method.getModifiers())) {
      throw new IllegalArgumentException("Must be public");
    } else if (Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("Cannot be static");
    }

    // Find definition by method name
    OperationDefinition operationDefinition =
        serviceDefinition.getOperations().values().stream()
            .filter(o -> method.getName().equals(o.getMethodName()))
            .findAny()
            .orElse(null);
    if (operationDefinition == null) {
      throw new IllegalStateException("Mo matching @Operation on the service interface");
    }

    // Invoke to get handler
    Object handler;
    try {
      handler = method.invoke(instance);
    } catch (Exception e) {
      throw new RuntimeException("Obtaining handler failed", e);
    }
    Objects.requireNonNull(handler);
    if (!(handler instanceof OperationHandler)) {
      throw new RuntimeException(
          "Expected handler to be instance of OperationHandler, was " + handler.getClass());
    }
    // Check the handler type
    ParameterizedType handleType = (ParameterizedType) method.getGenericReturnType();
    if (handleType.getRawType() != OperationHandler.class) {
      throw new IllegalArgumentException("Must return an OperationHandler");
    }
    if (handleType.getActualTypeArguments().length != 2) {
      // This should never happen, but just in case
      throw new IllegalArgumentException("OperationHandler must have two type arguments");
    }
    if (handleType.getActualTypeArguments()[0] != operationDefinition.getInputType()) {
      throw new IllegalArgumentException(
          "OperationHandler input type mismatch expected "
              + operationDefinition.getInputType().getTypeName()
              + " but got "
              + handleType.getActualTypeArguments()[0].getTypeName());
    }
    if (handleType.getActualTypeArguments()[1] != operationDefinition.getOutputType()) {
      throw new IllegalArgumentException(
          "OperationHandler output type mismatch expected "
              + operationDefinition.getOutputType().getTypeName()
              + " but got "
              + handleType.getActualTypeArguments()[1].getTypeName());
    }

    // Add to builder
    if (builder.operationHandlers.containsKey(operationDefinition.getName())) {
      throw new RuntimeException("Multiple overloads with @OperationImpl");
    }
    builder.putOperationHandler(operationDefinition.getName(), (OperationHandler<?, ?>) handler);
  }

  private static void collectClassMethods(Class<?> clazz, final List<Method> methods) {
    // Add all declared methods that may not be overridden in the method list already. We do a
    // simple check matching names and parameter types. This is not a full JVM virtual/override
    // check, but good enough since we don't allow generics in operations.
    Arrays.stream(clazz.getDeclaredMethods())
        .filter(
            method ->
                methods.stream()
                    .noneMatch(
                        superMethod ->
                            superMethod.getName().equals(method.getName())
                                && Arrays.equals(
                                    superMethod.getGenericParameterTypes(),
                                    method.getGenericParameterTypes())))
        .forEach(methods::add);
    // Do superclass
    if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
      collectClassMethods(clazz.getSuperclass(), methods);
    }
  }

  /** Create a builder for a service impl instance. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder for a service impl instance from an existing service impl instance. */
  public static Builder newBuilder(ServiceImplInstance instance) {
    return new Builder(instance);
  }

  private final ServiceDefinition definition;
  private final Map<String, OperationHandler<Object, Object>> operationHandlers;

  private ServiceImplInstance(
      ServiceDefinition definition,
      Map<String, OperationHandler<Object, Object>> operationHandlers) {
    this.definition = definition;
    this.operationHandlers = operationHandlers;
  }

  public ServiceDefinition getDefinition() {
    return definition;
  }

  public Map<String, OperationHandler<Object, Object>> getOperationHandlers() {
    return operationHandlers;
  }

  /** Builder for a service impl instance. */
  public static class Builder {
    private @Nullable ServiceDefinition definition;
    private final Map<String, OperationHandler<Object, Object>> operationHandlers;

    private Builder() {
      operationHandlers = new HashMap<>();
    }

    private Builder(ServiceImplInstance instance) {
      definition = instance.definition;
      operationHandlers = new HashMap<>(instance.operationHandlers);
    }

    /** Set service definition. Required. */
    public Builder setDefinition(ServiceDefinition definition) {
      this.definition = definition;
      return this;
    }

    /** Get operation handlers to mutate. */
    public Map<String, OperationHandler<Object, Object>> getOperationHandlers() {
      return operationHandlers;
    }

    /** Add operation handler. */
    @SuppressWarnings("unchecked")
    public Builder putOperationHandler(
        String operationName, OperationHandler<?, ?> operationHandler) {
      operationHandlers.put(operationName, (OperationHandler<Object, Object>) operationHandler);
      return this;
    }

    /** Build the instance. */
    public ServiceImplInstance build() {
      Objects.requireNonNull(definition, "Service definition required");
      if (operationHandlers.isEmpty()) {
        throw new IllegalStateException("No operation handlers defined");
      }

      // Check all operations have handlers and that all operations correspond to handlers
      SortedSet<String> forDiff = new TreeSet<>(definition.getOperations().keySet());
      forDiff.removeAll(operationHandlers.keySet());
      if (!forDiff.isEmpty()) {
        throw new IllegalStateException(
            "Missing handlers for service operations: " + String.join(", ", forDiff));
      }
      forDiff = new TreeSet<>(operationHandlers.keySet());
      forDiff.removeAll(definition.getOperations().keySet());
      if (!forDiff.isEmpty()) {
        throw new IllegalStateException(
            "Operation handlers don't correspond to service operations: "
                + String.join(", ", forDiff));
      }

      return new ServiceImplInstance(
          definition, Collections.unmodifiableMap(new HashMap<>(operationHandlers)));
    }
  }
}
