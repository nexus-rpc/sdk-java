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

  private final String operationToken;

  private OperationFetchInfoDetails(String operationId) {
    this.operationToken = operationId;
  }

  /** ID of the operation. */
  public String getOperationToken() {
    return operationToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationFetchInfoDetails that = (OperationFetchInfoDetails) o;
    return Objects.equals(operationToken, that.operationToken);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(operationToken);
  }

  @Override
  public String toString() {
    return "OperationFetchInfoDetails{" + "operationToken='" + operationToken + '\'' + '}';
  }

  /** Builder for operation fetch info details. */
  public static class Builder {
    private @Nullable String operationToken;

    private Builder() {}

    private Builder(OperationFetchInfoDetails details) {
      operationToken = details.operationToken;
    }

    /** Set operation token. Required. */
    public Builder setOperationToken(String operationToken) {
      this.operationToken = operationToken;
      return this;
    }

    /** Build the details. */
    public OperationFetchInfoDetails build() {
      Objects.requireNonNull(operationToken, "Operation Token required");
      return new OperationFetchInfoDetails(operationToken);
    }
  }
}
