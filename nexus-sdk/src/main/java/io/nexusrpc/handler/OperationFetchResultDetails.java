package io.nexusrpc.handler;

import io.nexusrpc.OperationStillRunningException;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Details for handling operation fetch result. */
public class OperationFetchResultDetails {
  /** Create a builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder from an existing set of details. */
  public static Builder newBuilder(OperationFetchResultDetails details) {
    return new Builder(details);
  }

  private final String operationId;
  private final @Nullable Duration timeout;

  private OperationFetchResultDetails(String operationId, @Nullable Duration timeout) {
    this.operationId = operationId;
    this.timeout = timeout;
  }

  /** ID of the operation. */
  public String getOperationId() {
    return operationId;
  }

  /**
   * Optional timeout for how long the user wants to wait on the result.
   *
   * <p>If this value is null, the result or {@link OperationStillRunningException} should be
   * returned/thrown right away. If this value is present, the fetch result call should try to wait
   * up until this duration or until an implementer chosen maximum, whichever ends sooner, before
   * returning the result or throwing {@link OperationStillRunningException}.
   */
  public @Nullable Duration getTimeout() {
    return timeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationFetchResultDetails that = (OperationFetchResultDetails) o;
    return Objects.equals(operationId, that.operationId) && Objects.equals(timeout, that.timeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operationId, timeout);
  }

  @Override
  public String toString() {
    return "OperationFetchResultDetails{"
        + "operationId='"
        + operationId
        + '\''
        + ", timeout="
        + timeout
        + '}';
  }

  /** Builder for operation fetch result details. */
  public static class Builder {
    private @Nullable String operationId;
    private @Nullable Duration timeout;

    private Builder() {}

    private Builder(OperationFetchResultDetails details) {
      operationId = details.operationId;
    }

    /** Set operation ID. Required. */
    public Builder setOperationId(String operationId) {
      this.operationId = operationId;
      return this;
    }

    /** Set timeout. */
    public Builder setTimeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public OperationFetchResultDetails build() {
      Objects.requireNonNull(operationId, "Operation ID required");
      return new OperationFetchResultDetails(operationId, timeout);
    }
  }
}
