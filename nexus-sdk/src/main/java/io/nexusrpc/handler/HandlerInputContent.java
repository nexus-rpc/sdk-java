package io.nexusrpc.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

/** Content that can be fixed or streaming for start operation input. */
public class HandlerInputContent {
  /** Create a builder for content. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final AtomicReference<InputStream> dataStream;
  private final Map<String, String> headers;

  private HandlerInputContent(InputStream dataStream, Map<String, String> headers) {
    this.dataStream = new AtomicReference<>(dataStream);
    this.headers = headers;
  }

  /**
   * Consume stream. Once called, this cannot be called again nor can {@link #consumeBytes} be
   * called. Users should not close this stream, it is closed externally when the operation method
   * is complete. Therefore, the InputStream cannot be used after the operation method is complete.
   */
  public InputStream consumeStream() {
    InputStream stream = dataStream.getAndSet(null);
    if (stream == null) {
      throw new IllegalStateException("Data already consumed");
    }
    return stream;
  }

  /**
   * Consume the bytes. This is basically a helper for {@link #consumeStream}. Once called, this
   * cannot be called again nor can {@link #consumeStream} be called, even if this method throws an
   * exception.
   */
  public byte[] consumeBytes() throws IOException {
    InputStream stream = consumeStream();
    // Collect entire input stream as byte array. This is unfortunately the best Java-8-safe
    // approach.
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[1024];
    while ((nRead = stream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    return buffer.toByteArray();
  }

  /** Headers. The returned map operates without regard to case. */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /** Builder for content. */
  public static class Builder {
    private @Nullable InputStream dataStream;
    private final SortedMap<String, String> headers;

    private Builder() {
      headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /** Set data stream. Required. */
    public Builder setDataStream(InputStream data) {
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

    public HandlerInputContent build() {
      Objects.requireNonNull(dataStream, "Data stream required");
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
      return new HandlerInputContent(
          dataStream, Collections.unmodifiableMap(new TreeMap<>(normalizedHeaders)));
    }
  }
}
