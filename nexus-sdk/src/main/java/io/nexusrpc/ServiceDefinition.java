package io.nexusrpc;

import java.util.*;

public class ServiceDefinition {
  public static ServiceDefinition fromClass(Class<?> clazz) {
    throw new UnsupportedOperationException("TODO");
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(ServiceDefinition definition) {
    return new Builder(definition);
  }

  private final String name;
  private final List<OperationDefinition> operations;

  private ServiceDefinition(String name, List<OperationDefinition> operations) {
    this.name = name;
    this.operations = operations;
  }

  public String getName() {
    return name;
  }

  public List<OperationDefinition> getOperations() {
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

  public static class Builder {
    private String name;
    private final List<OperationDefinition> operations = new ArrayList<>();

    private Builder() {}

    private Builder(ServiceDefinition definition) {
      if (definition == null) {
        return;
      }
      this.name = definition.name;
      this.operations.addAll(definition.operations);
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<OperationDefinition> getOperations() {
      return operations;
    }

    public ServiceDefinition build() {
      return new ServiceDefinition(name, Collections.unmodifiableList(new ArrayList<>(operations)));
    }
  }
}
