package io.nexusrpc;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Information about an operation. */
@Experimental
public class OperationInfo {
  /** Create a builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder from existing info. */
  public static Builder newBuilder(OperationInfo info) {
    return new Builder(info);
  }

  private final String token;
  private final OperationState state;

  private OperationInfo(String token, OperationState state) {
    this.token = token;
    this.state = state;
  }

  /** State of the operation. */
  public OperationState getState() {
    return state;
  }

  /** Token of the operation. */
  public String getToken() {
    return token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationInfo that = (OperationInfo) o;
    return Objects.equals(token, that.token) && state == that.state;
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, state);
  }

  @Override
  public String toString() {
    return "OperationInfo{" + "token='" + token + '\'' + ", state=" + state + '}';
  }

  /** Builder for operation info. */
  public static class Builder {
    @Nullable private String token;
    @Nullable private OperationState state;

    private Builder() {}

    private Builder(OperationInfo info) {
      token = info.token;
      state = info.state;
    }

    /** Set operation token. Required. */
    public Builder setToken(String token) {
      this.token = token;
      return this;
    }

    /** Set operation state. Required. */
    public Builder setState(OperationState state) {
      this.state = state;
      return this;
    }

    /** Build the info. */
    public OperationInfo build() {
      Objects.requireNonNull(token, "Operation Token required");
      Objects.requireNonNull(state, "State required");
      return new OperationInfo(token, state);
    }
  }
}
