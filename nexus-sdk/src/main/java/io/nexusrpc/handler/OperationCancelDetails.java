package io.nexusrpc.handler;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Details for handling operation cancel. */
public class OperationCancelDetails {

  /** Create a builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder from an existing set of details. */
  public static Builder newBuilder(OperationCancelDetails details) {
    return new Builder(details);
  }

  private final String operationId;

  private OperationCancelDetails(String operationId) {
    this.operationId = operationId;
  }

  /** ID of the operation. */
  public String getOperationId() {
    return operationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationCancelDetails that = (OperationCancelDetails) o;
    return Objects.equals(operationId, that.operationId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(operationId);
  }

  @Override
  public String toString() {
    return "OperationCancelDetails{" + "operationId='" + operationId + '\'' + '}';
  }

  /** Builder for operation cancel details. */
  public static class Builder {
    private @Nullable String operationId;

    private Builder() {}

    private Builder(OperationCancelDetails details) {
      operationId = details.operationId;
    }

    /** Set operation ID. Required. */
    public Builder setOperationId(String operationId) {
      this.operationId = operationId;
      return this;
    }

    /** Build the details. */
    public OperationCancelDetails build() {
      Objects.requireNonNull(operationId, "Operation ID required");
      return new OperationCancelDetails(operationId);
    }
  }
}
