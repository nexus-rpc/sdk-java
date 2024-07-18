package io.nexusrpc;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class OperationDefinition {
  public static OperationDefinition fromMethod(Method method) {
    throw new UnsupportedOperationException("TODO");
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(OperationDefinition definition) {
    return new Builder(definition);
  }

  private final String name;
  private final boolean async;
  private final Optional<Type> inputType;
  private final Type outputType;

  private OperationDefinition(
      String name, boolean async, Optional<Type> inputType, Type outputType) {
    this.name = name;
    this.async = async;
    this.inputType = inputType;
    this.outputType = outputType;
  }

  public String getName() {
    return name;
  }

  public boolean isAsync() {
    return async;
  }

  public Optional<Type> getInputType() {
    return inputType;
  }

  public Type getOutputType() {
    return outputType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationDefinition that = (OperationDefinition) o;
    return async == that.async
        && Objects.equals(name, that.name)
        && Objects.equals(inputType, that.inputType)
        && Objects.equals(outputType, that.outputType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, async, inputType, outputType);
  }

  @Override
  public String toString() {
    return "OperationDefinition{"
        + "name='"
        + name
        + '\''
        + ", async="
        + async
        + ", inputType="
        + inputType
        + ", outputType="
        + outputType
        + '}';
  }

  public static class Builder {
    private String name;
    private boolean async;
    private Optional<Type> inputType;
    private Type outputType;

    private Builder() {}

    private Builder(OperationDefinition definition) {
      if (definition == null) {
        return;
      }
      this.name = definition.name;
      this.async = definition.async;
      this.inputType = definition.inputType;
      this.outputType = definition.outputType;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setAsync(boolean async) {
      this.async = async;
    }

    public void setInputType(Optional<Type> inputType) {
      this.inputType = inputType;
    }

    public void setOutputType(Type outputType) {
      this.outputType = outputType;
    }

    public OperationDefinition build() {
      return new OperationDefinition(name, async, inputType, outputType);
    }
  }
}
