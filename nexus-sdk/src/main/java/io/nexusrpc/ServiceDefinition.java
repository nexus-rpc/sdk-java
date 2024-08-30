package io.nexusrpc;

import java.lang.reflect.Method;
import java.util.*;
import org.jspecify.annotations.Nullable;

/** Definition of a service with operations. */
public class ServiceDefinition {
  /**
   * Create a service definition from a {@link Service} annotated interface. This will fail if the
   * service interface is invalid according to rules documented on the annotation.
   */
  public static ServiceDefinition fromClass(Class<?> clazz) {
    Service service = clazz.getDeclaredAnnotation(Service.class);
    if (service == null) {
      throw new IllegalArgumentException("Missing @Service annotation");
    } else if (!clazz.isInterface()) {
      throw new IllegalArgumentException("Must be an interface");
    }
    String name = service.name().isEmpty() ? clazz.getSimpleName() : service.name();
    Builder builder = newBuilder().setName(name);

    // Collect all interfaces and declared methods (use linked hash set to keep in deterministic
    // order). Set of methods for each item are ones that can override each other.
    Set<Class<?>> interfaces = new LinkedHashSet<>();
    List<List<Method>> methods = new ArrayList<>();
    collectInterfaceInfo(clazz, interfaces, methods);

    // Check that if any interfaces have a service annotation, it matches this one
    for (Class<?> iface : interfaces) {
      Service subService = iface.getDeclaredAnnotation(Service.class);
      if (subService != null) {
        String subName = subService.name().isEmpty() ? iface.getSimpleName() : subService.name();
        if (!name.equals(subName)) {
          throw new IllegalArgumentException(
              "Interface "
                  + iface.getName()
                  + " has a service annotation whose name ("
                  + subName
                  + ") does not match the expected name on the final interface ("
                  + name
                  + ")");
        }
      }
    }

    // Build operations, collecting failures
    List<String> operationFailures = new ArrayList<>();
    for (List<Method> methodSet : methods) {
      // Every method must be an operation, so we make definitions for all, and
      // in any case where the definition doesn't match, it's an error.
      OperationDefinition firstOperation = null;
      for (Method method : methodSet) {
        try {
          OperationDefinition thisOperation = OperationDefinition.fromMethod(method);
          if (firstOperation == null) {
            firstOperation = thisOperation;
            builder.addOperation(firstOperation);
          } else if (!firstOperation.equals(thisOperation)) {
            operationFailures.add(
                "Operation definition on "
                    + method.getName()
                    + " on "
                    + method.getDeclaringClass().getName()
                    + " mismatches against another operation of the same name/signature in the hierarchy");
            break;
          }
        } catch (Exception e) {
          operationFailures.add(
              "Operation definition on "
                  + method.getName()
                  + " on "
                  + method.getDeclaringClass().getName()
                  + " is invalid: "
                  + e.getMessage());
          break;
        }
      }
    }

    // Fail if there are any operation failures
    if (!operationFailures.isEmpty()) {
      throw new IllegalArgumentException(
          operationFailures.size()
              + " operation(s) were invalid, reasons: "
              + String.join(", ", operationFailures));
    }
    // Let the service builder fail for the rest
    return builder.build();
  }

  // Every method in the list is a matching override
  private static void collectInterfaceInfo(
      Class<?> iface, Set<Class<?>> interfaces, List<List<Method>> methods) {
    interfaces.add(iface);
    for (Method method : iface.getDeclaredMethods()) {
      // Try to find existing method that matches the name and parameter types
      // as a poor-man's virtual/override check (we don't allow generics anyway).
      // Otherwise, add as a new list.
      methods.stream()
          .filter(
              possible ->
                  possible.get(0).getName().equals(method.getName())
                      && Arrays.equals(
                          possible.get(0).getParameterTypes(), method.getParameterTypes()))
          .findFirst()
          .orElseGet(
              () -> {
                List<Method> newList = new ArrayList<>();
                methods.add(newList);
                return newList;
              })
          .add(method);
    }
    for (Class<?> parent : iface.getInterfaces()) {
      if (!interfaces.contains(parent)) {
        collectInterfaceInfo(parent, interfaces, methods);
      }
    }
  }

  /** Create a builder for a service definition. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder for a service definition from an existing service definition. */
  public static Builder newBuilder(ServiceDefinition definition) {
    return new Builder(definition);
  }

  private final String name;
  private final Map<String, OperationDefinition> operations;

  private ServiceDefinition(String name, Map<String, OperationDefinition> operations) {
    this.name = name;
    this.operations = operations;
  }

  /** Service name. */
  public String getName() {
    return name;
  }

  /** Collection of operations by name. */
  public Map<String, OperationDefinition> getOperations() {
    return operations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServiceDefinition that = (ServiceDefinition) o;
    return Objects.equals(name, that.name) && Objects.equals(operations, that.operations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, operations);
  }

  @Override
  public String toString() {
    return "ServiceDefinition{" + "name='" + name + '\'' + ", operations=" + operations + '}';
  }

  /** Builder for a service definition. */
  public static class Builder {
    @Nullable private String name;
    private final List<OperationDefinition> operations;

    private Builder() {
      operations = new ArrayList<>();
    }

    private Builder(ServiceDefinition definition) {
      name = definition.name;
      // Order does not matter here
      operations = new ArrayList<>(definition.operations.values());
    }

    /** Set service name. Required. */
    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    /** Get operations, at least one required. */
    public List<OperationDefinition> getOperations() {
      return operations;
    }

    /** Add operations, at least one required. */
    public Builder addOperation(OperationDefinition definition) {
      operations.add(definition);
      return this;
    }

    /** Build the service definition. */
    public ServiceDefinition build() {
      Objects.requireNonNull(name, "Name required");
      if (operations.isEmpty()) {
        throw new IllegalStateException("No operations defined");
      }
      Map<String, OperationDefinition> definitions = new HashMap<>(operations.size());
      for (OperationDefinition operation : operations) {
        if (definitions.containsKey(operation.getName())) {
          throw new IllegalStateException(
              "Multiple operations named '" + operation.getName() + "'");
        }
        if (operation.getMethodName() != null) {
          if (definitions.values().stream()
              .anyMatch(other -> operation.getMethodName().equals(other.getMethodName()))) {
            throw new IllegalStateException(
                "Multiple operations on the same method name of '"
                    + operation.getMethodName()
                    + "'");
          }
        }
        definitions.put(operation.getName(), operation);
      }
      return new ServiceDefinition(name, Collections.unmodifiableMap(definitions));
    }
  }
}
