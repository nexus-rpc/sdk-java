package io.nexusrpc.handler;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import io.nexusrpc.Experimental;
import org.jspecify.annotations.Nullable;

/** Content that can be fixed or streaming as a result of an operation. */
@Experimental
public class HandlerResultContent {
  /** Create a builder for content. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder for content from existing content. */
  public static Builder newBuilder(HandlerResultContent content) {
    return new Builder(content);
  }

  private final byte @Nullable [] dataBytes;
  private final @Nullable InputStream dataStream;
  private final Map<String, String> headers;

  private HandlerResultContent(
      byte @Nullable [] dataBytes, @Nullable InputStream dataStream, Map<String, String> headers) {
    this.dataBytes = dataBytes;
    this.dataStream = dataStream;
    this.headers = headers;
  }

  /** Data bytes. This or {@link #getDataStream} is non-null, but not both. */
  public byte @Nullable [] getDataBytes() {
    return dataBytes;
  }

  /** Data bytes. This or {@link #getDataBytes} is non-null, but not both. */
  public @Nullable InputStream getDataStream() {
    return dataStream;
  }

  /** Headers. The returned map operates without regard to case. */
  public Map<String, String> getHeaders() {
    return headers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HandlerResultContent that = (HandlerResultContent) o;
    return Objects.deepEquals(dataBytes, that.dataBytes)
        && Objects.equals(dataStream, that.dataStream)
        && Objects.equals(headers, that.headers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Arrays.hashCode(dataBytes), dataStream, headers);
  }

  /** Builder for content. */
  public static class Builder {
    private byte @Nullable [] dataBytes;
    private @Nullable InputStream dataStream;
    private final SortedMap<String, String> headers;

    private Builder() {
      headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    private Builder(HandlerResultContent content) {
      dataBytes = content.dataBytes;
      dataStream = content.dataStream;
      headers = new TreeMap<>(content.headers);
    }

    /** Set data. Unsets any data set before. Required. */
    public Builder setData(byte[] data) {
      dataBytes = data;
      dataStream = null;
      return this;
    }

    /** Set data. Unsets any data set before. Required. */
    public Builder setData(InputStream data) {
      dataBytes = null;
      dataStream = data;
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

    public HandlerResultContent build() {
      if (dataStream == null) {
        Objects.requireNonNull(dataBytes, "Data required");
      }
      // TODO(cretz): Most of the time the headers come over immutable
      // anyways, are we unnecessarily introducing overhead copying them every
      // time?
      SortedMap<String, String> normalizedHeaders =
          headers.entrySet().stream()
              .collect(
                  Collectors.toMap(
                      (k) -> k.getKey().toLowerCase(),
                      Map.Entry::getValue,
                      (a, b) -> a,
                      () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));
      return new HandlerResultContent(
          dataBytes, dataStream, Collections.unmodifiableMap(new TreeMap<>(normalizedHeaders)));
    }
  }
}
