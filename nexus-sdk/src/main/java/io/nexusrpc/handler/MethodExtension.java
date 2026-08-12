package io.nexusrpc.handler;

import io.nexusrpc.Experimental;
import io.nexusrpc.OperationDefinition;
import java.lang.reflect.Method;
import org.jspecify.annotations.Nullable;

/**
 * Extension point for {@link ServiceImplInstance#fromInstance(Object, java.util.List)} that lets a
 * framework recognize its own operation-handler annotation alongside {@link OperationImpl}.
 *
 * <p>Extensions are invoked for each method without {@link OperationImpl} that maps by method name
 * to an operation on the service. If the method belongs to the extension's annotation, the
 * extension returns its handler; otherwise it returns {@code null} so the next extension can be
 * consulted.
 *
 * <p>Extensions are responsible for their own validation of method shape (parameter types, return
 * type, modifiers) and for translating the method into an {@link OperationHandler}.
 */
@Experimental
public interface MethodExtension {

  /**
   * Inspect {@code method} on {@code instance}. Return an operation handler if the method is
   * recognized, or {@code null} to defer to the next extension.
   *
   * @param instance the service implementation instance
   * @param method the method being scanned
   * @param operationDefinition the operation matched to the method by method name
   * @return the operation handler, or {@code null} if the method is not recognized
   */
  @Nullable OperationHandler<?, ?> extract(
      Object instance, Method method, OperationDefinition operationDefinition);
}
