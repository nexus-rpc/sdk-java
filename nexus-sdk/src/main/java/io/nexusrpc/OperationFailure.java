package io.nexusrpc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Information about an operation failure. */
public class OperationFailure {
  private final String message;
  private final Map<String, String> metadata;
  private final @Nullable Object details;

  /** Create a builder for an operation failure. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder for an operation failure from an existing operation failure. */
  public static Builder newBuilder(OperationFailure failure) {
    return new Builder(failure);
  }

  private OperationFailure(String message, Map<String, String> metadata, @Nullable Object details) {
    this.message = message;
    this.metadata = metadata;
    this.details = details;
  }

  /** Failure message. */
  public String getMessage() {
    return message;
  }

  /** Failure metadata. */
  public Map<String, String> getMetadata() {
    return metadata;
  }

  /** Failure details. */
  public @Nullable Object getDetails() {
    return details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationFailure that = (OperationFailure) o;
    return Objects.equals(message, that.message)
        && Objects.equals(metadata, that.metadata)
        && Objects.equals(details, that.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, metadata, details);
  }

  @Override
  public String toString() {
    return "OperationFailure{"
        + "message='"
        + message
        + '\''
        + ", metadata="
        + metadata
        + ", details="
        + details
        + '}';
  }

  /** Builder for an operation failure. */
  public static class Builder {
    private @Nullable String message;
    private final Map<String, String> metadata;
    private @Nullable Object details;

    private Builder() {
      metadata = new HashMap<>();
    }

    private Builder(OperationFailure failure) {
      message = failure.message;
      metadata = new HashMap<>(failure.metadata);
      details = failure.details;
    }

    /** Set message, required. */
    public Builder setMessage(String message) {
      this.message = message;
      return this;
    }

    /** Get metadata to mutate. */
    public Map<String, String> getMetadata() {
      return metadata;
    }

    /** Add a single metadata key/value. */
    public Builder putMetadata(String key, String value) {
      metadata.put(key, value);
      return this;
    }

    /** Set details. */
    public Builder setDetails(@Nullable Object details) {
      this.details = details;
      return this;
    }

    /** Build the operation failure. */
    public OperationFailure build() {
      Objects.requireNonNull(message, "Message required");
      return new OperationFailure(
          message, Collections.unmodifiableMap(new HashMap<>(metadata)), details);
    }
  }
}
