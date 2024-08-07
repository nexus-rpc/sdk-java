package io.nexusrpc;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Information about an operation. */
public class OperationInfo {
  private final String id;
  private final OperationState state;

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(OperationInfo info) {
    return new Builder(info);
  }

  private OperationInfo(String id, OperationState state) {
    this.id = id;
    this.state = state;
  }

  public String getId() {
    return id;
  }

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

  public static class Builder {
    @Nullable private String id;
    @Nullable private OperationState state;

    private Builder() {}

    private Builder(OperationInfo info) {
      id = info.id;
      state = info.state;
    }

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setState(OperationState state) {
      this.state = state;
      return this;
    }

    public OperationInfo build() {
      Objects.requireNonNull(id, "ID required");
      Objects.requireNonNull(state, "State required");
      return new OperationInfo(id, state);
    }
  }
}
