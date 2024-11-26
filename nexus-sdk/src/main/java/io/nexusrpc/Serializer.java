package io.nexusrpc;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

/** Serializer used to convert values to bytes and vice-versa. */
public interface Serializer {
  /** Serialize the value as content. */
  Content serialize(@Nullable Object value);

  /** Deserialize the content as a value of the given type. */
  @Nullable Object deserialize(Content content, Type type);

  /** Data + headers used by serializers. */
  class Content {
    /** Create a builder for content. */
    public static Builder newBuilder() {
      return new Builder();
    }

    /** Create a builder for content from existing content. */
    public static Builder newBuilder(Content content) {
      return new Builder(content);
    }

    // TODO(cretz): Is this acceptable over ByteBuffer for our use case?
    private final byte[] data;
    private final Map<String, String> headers;

    private Content(byte[] data, Map<String, String> headers) {
      this.data = data;
      this.headers = headers;
    }

    /** Data. */
    public byte[] getData() {
      return data;
    }

    /** Headers. The returned map operates without regard to case. */
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Content content = (Content) o;
      return Objects.deepEquals(data, content.data) && Objects.equals(headers, content.headers);
    }

    @Override
    public int hashCode() {
      return Objects.hash(Arrays.hashCode(data), headers);
    }

    /** Builder for content. */
    public static class Builder {
      private byte @Nullable [] data;
      private final SortedMap<String, String> headers;

      private Builder() {
        headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      }

      private Builder(Content content) {
        data = content.data;
        headers = new TreeMap<>(content.headers);
      }

      /** Set data. Required. */
      public Builder setData(byte[] data) {
        this.data = data;
        return this;
      }

      /** Get headers to mutate. */
      public Map<String, String> getHeaders() {
        return headers;
      }

      /** Add header. */
      public Builder putHeader(String key, String value) {
        headers.put(key, value);
        return this;
      }

      public Content build() {
        Objects.requireNonNull(data, "Data required");
        // TODO(cretz): Most of the time the headers come over immutable
        // anyways, are we unnecessarily introducing overhead copying them every
        // time?
        Map<String, String> normalizedHeaders =
            headers.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toLowerCase(), entry -> entry.getValue()));
        return new Content(data, Collections.unmodifiableMap(new TreeMap<>(normalizedHeaders)));
      }
    }
  }
}
