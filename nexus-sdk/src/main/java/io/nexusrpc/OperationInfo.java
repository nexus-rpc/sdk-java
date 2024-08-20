package io.nexusrpc;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Information about an operation. */
public class OperationInfo {
  /** Create a builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder from existing info. */
  public static Builder newBuilder(OperationInfo info) {
    return new Builder(info);
  }

  private final String id;
  private final OperationState state;

  private OperationInfo(String id, OperationState state) {
    this.id = id;
    this.state = state;
  }

  /** ID of the operation. */
  public String getId() {
    return id;
  }

  /** State of the operation. */
  public OperationState getState() {
    return state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationInfo that = (OperationInfo) o;
    return Objects.equals(id, that.id) && state == that.state;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, state);
  }

  @Override
  public String toString() {
    return "OperationInfo{" + "id='" + id + '\'' + ", state=" + state + '}';
  }

  /** Builder for operation info. */
  public static class Builder {
    @Nullable private String id;
    @Nullable private OperationState state;

    private Builder() {}

    private Builder(OperationInfo info) {
      id = info.id;
      state = info.state;
    }

    /** Set operation ID. Required. */
    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    /** Set operation state. Required. */
    public Builder setState(OperationState state) {
      this.state = state;
      return this;
    }

    /** Build the info. */
    public OperationInfo build() {
      Objects.requireNonNull(id, "ID required");
      Objects.requireNonNull(state, "State required");
      return new OperationInfo(id, state);
    }
  }
}
