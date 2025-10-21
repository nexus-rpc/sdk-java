package io.nexusrpc.client.transport;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CancelOperationOptions {
  /** Create a builder for CancelOperationOptions. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final SortedMap<String, String> headers;

  private CancelOperationOptions(SortedMap<String, String> headers) {
    this.headers = headers;
  }

  /** Headers. The returned map operates without regard to case. */
  public Map<String, String> getHeaders() {
    return headers;
  }

  public static class Builder {
    private final SortedMap<String, String> headers;

    private Builder() {
      headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /** Get headers to mutate. */
    public Map<String, String> getHeaders() {
      return headers;
    }

    public CancelOperationOptions build() {
      return new CancelOperationOptions(headers);
    }
  }
}
