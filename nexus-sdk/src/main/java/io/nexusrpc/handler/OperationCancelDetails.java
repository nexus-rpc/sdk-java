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

  private final String operationToken;

  private OperationCancelDetails(String operationId) {
    this.operationToken = operationId;
  }

  /** Get the operation token. */
  public String getOperationToken() {
    return operationToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationCancelDetails that = (OperationCancelDetails) o;
    return Objects.equals(operationToken, that.operationToken);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(operationToken);
  }

  @Override
  public String toString() {
    return "OperationCancelDetails{" + "operationToken='" + operationToken + '\'' + '}';
  }

  /** Builder for operation cancel details. */
  public static class Builder {
    private @Nullable String operationToken;

    private Builder() {}

    private Builder(OperationCancelDetails details) {
      operationToken = details.operationToken;
    }

    /** Set operation token. Required. */
    public Builder setOperationToken(String operationToken) {
      this.operationToken = operationToken;
      return this;
    }

    /** Build the details. */
    public OperationCancelDetails build() {
      Objects.requireNonNull(operationToken, "Operation Token is required");
      return new OperationCancelDetails(operationToken);
    }
  }
}
