package io.nexusrpc.handler;

import io.nexusrpc.ServiceDefinition;
import java.lang.reflect.Method;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Extension point for {@link ServiceImplInstance#fromInstance(Object, java.util.List)} that lets a
 * framework recognize its own operation-handler annotation alongside {@link OperationImpl}.
 *
 * <p>For each method discovered during scanning of a {@link ServiceImpl}-annotated class, the
 * extension's {@link #extract} is called once. If the method belongs to the extension's annotation,
 * the extension returns a {@link Result} describing the operation name and handler; otherwise it
 * returns {@code null}. Duplicate operation registrations (across both {@link OperationImpl} and
 * any extension) are caught by the builder.
 *
 * <p>Extensions are responsible for their own validation of method shape (parameter types, return
 * type, modifiers) and for translating the method into an {@link OperationHandler}.
 */
public interface MethodExtension {

  /**
   * Inspect {@code method} on {@code instance}. Return a {@link Result} to register an operation
   * handler, or {@code null} if the method is not claimed by this extension.
   *
   * @param instance the service implementation instance
   * @param method the method being scanned
   * @param serviceDefinition the resolved service definition for the {@code @ServiceImpl}
   * @return a result describing the operation, or {@code null} to skip
   */
  @Nullable Result extract(Object instance, Method method, ServiceDefinition serviceDefinition);

  /** Result returned by an extension that claims a method. */
  final class Result {
    private final String operationName;
    private final OperationHandler<?, ?> handler;

    public Result(String operationName, OperationHandler<?, ?> handler) {
      this.operationName = Objects.requireNonNull(operationName, "operationName");
      this.handler = Objects.requireNonNull(handler, "handler");
    }

    public String getOperationName() {
      return operationName;
    }

    public OperationHandler<?, ?> getHandler() {
      return handler;
    }
  }
}
