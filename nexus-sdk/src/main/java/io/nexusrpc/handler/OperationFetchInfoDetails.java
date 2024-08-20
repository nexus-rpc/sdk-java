package io.nexusrpc.handler;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Details for handling operation fetch info. */
public class OperationFetchInfoDetails {
  /** Create a builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder from an existing set of details. */
  public static Builder newBuilder(OperationFetchInfoDetails details) {
    return new Builder(details);
  }

  private final String operationId;

  private OperationFetchInfoDetails(String operationId) {
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
    OperationFetchInfoDetails that = (OperationFetchInfoDetails) o;
    return Objects.equals(operationId, that.operationId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(operationId);
  }

  @Override
  public String toString() {
    return "OperationFetchInfoDetails{" + "operationId='" + operationId + '\'' + '}';
  }

  /** Builder for operation fetch info details. */
  public static class Builder {
    private @Nullable String operationId;

    private Builder() {}

    private Builder(OperationFetchInfoDetails details) {
      operationId = details.operationId;
    }

    /** Set operation ID. Required. */
    public Builder setOperationId(String operationId) {
      this.operationId = operationId;
      return this;
    }

    /** Build the details. */
    public OperationFetchInfoDetails build() {
      Objects.requireNonNull(operationId, "Operation ID required");
      return new OperationFetchInfoDetails(operationId);
    }
  }
}
