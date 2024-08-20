package io.nexusrpc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Definition of an operation on a service. */
public class OperationDefinition {
  static OperationDefinition fromMethod(Method method) {
    Operation operation = method.getDeclaredAnnotation(Operation.class);
    if (operation == null) {
      throw new IllegalArgumentException("Missing @Operation annotation");
    } else if (method.getParameterCount() > 1) {
      throw new IllegalArgumentException("Can have no more than one parameter");
    } else if (method.getTypeParameters().length > 0) {
      throw new IllegalArgumentException("Cannot be generic");
    } else if (method.getExceptionTypes().length > 0) {
      throw new IllegalArgumentException("Cannot have throws clause");
    } else if (method.isDefault() && !method.isSynthetic()) {
      throw new IllegalArgumentException("Cannot have default implementation");
    } else if (Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("Cannot be static");
    }
    return newBuilder()
        .setName(operation.name().isEmpty() ? method.getName() : operation.name())
        .setMethodName(method.getName())
        .setInputType(method.getParameterCount() == 0 ? Void.TYPE : method.getParameterTypes()[0])
        .setOutputType(method.getGenericReturnType())
        .build();
  }

  /** Create a builder for an operation definition. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder for an operation definition from an existing operation definition. */
  public static Builder newBuilder(OperationDefinition definition) {
    return new Builder(definition);
  }

  private final String name;
  private final @Nullable String methodName;
  private final Type inputType;
  private final Type outputType;

  private OperationDefinition(
      String name, @Nullable String methodName, Type inputType, Type outputType) {
    this.name = name;
    this.methodName = methodName;
    this.inputType = inputType;
    this.outputType = outputType;
  }

  /** Operation name. */
  public String getName() {
    return name;
  }

  /**
   * Method name the operation is on if this was created via reflection. This is mostly used
   * internally to match with implementation.
   */
  public @Nullable String getMethodName() {
    return methodName;
  }

  /** Input type. Will be {@link Void#TYPE} if no input type. */
  public Type getInputType() {
    return inputType;
  }

  /** Output type. Will be {@link Void#TYPE} if void return. */
  public Type getOutputType() {
    return outputType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationDefinition that = (OperationDefinition) o;
    return Objects.equals(name, that.name)
        && Objects.equals(methodName, that.methodName)
        && Objects.equals(inputType, that.inputType)
        && Objects.equals(outputType, that.outputType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, methodName, inputType, outputType);
  }

  @Override
  public String toString() {
    return "OperationDefinition{"
        + "name='"
        + name
        + '\''
        + ", methodName='"
        + methodName
        + '\''
        + ", inputType="
        + inputType
        + ", outputType="
        + outputType
        + '}';
  }

  /** Builder for an operation definition. */
  public static class Builder {
    @Nullable private String name;
    @Nullable private String methodName;
    @Nullable private Type inputType;
    @Nullable private Type outputType;

    private Builder() {}

    private Builder(OperationDefinition definition) {
      name = definition.name;
      methodName = definition.methodName;
      inputType = definition.inputType;
      outputType = definition.outputType;
    }

    /** Set operation name. Required. */
    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    /** Set reflected operation method name. Optional. */
    public Builder setMethodName(String methodName) {
      this.methodName = methodName;
      return this;
    }

    /** Set input type.Required. */
    public Builder setInputType(Type inputType) {
      this.inputType = inputType;
      return this;
    }

    /** Set output type.Required. */
    public Builder setOutputType(Type outputType) {
      this.outputType = outputType;
      return this;
    }

    /** Build the operation definition. */
    public OperationDefinition build() {
      Objects.requireNonNull(name, "Name required");
      Objects.requireNonNull(inputType, "Input type required");
      Objects.requireNonNull(outputType, "Output type required");
      return new OperationDefinition(name, methodName, inputType, outputType);
    }
  }
}
