package io.nexusrpc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/** Information about an operation failure. */
public class FailureInfo {

  /** Create a builder for an operation failure. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder for an operation failure from an existing operation failure. */
  public static Builder newBuilder(FailureInfo failure) {
    return new Builder(failure);
  }

  private final String message;
  private final Map<String, String> metadata;
  private final @Nullable String detailsJson;

  private FailureInfo(String message, Map<String, String> metadata, @Nullable String detailsJson) {
    this.message = message;
    this.metadata = metadata;
    this.detailsJson = detailsJson;
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
  public @Nullable String getDetailsJson() {
    return detailsJson;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FailureInfo that = (FailureInfo) o;
    return Objects.equals(message, that.message)
        && Objects.equals(metadata, that.metadata)
        && Objects.equals(detailsJson, that.detailsJson);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, metadata, detailsJson);
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
        + detailsJson
        + '}';
  }

  /** Builder for an operation failure. */
  public static class Builder {
    private @Nullable String message;
    private final Map<String, String> metadata;
    private @Nullable String detailsJson;

    private Builder() {
      metadata = new HashMap<>();
    }

    private Builder(FailureInfo failure) {
      message = failure.message;
      metadata = new HashMap<>(failure.metadata);
      detailsJson = failure.detailsJson;
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
    public Builder setDetailsJson(@Nullable String detailsJson) {
      this.detailsJson = detailsJson;
      return this;
    }

    /** Build the operation failure. */
    public FailureInfo build() {
      Objects.requireNonNull(message, "Message required");
      return new FailureInfo(
          message, Collections.unmodifiableMap(new HashMap<>(metadata)), detailsJson);
    }
  }
}
