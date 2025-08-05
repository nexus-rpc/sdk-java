package io.nexusrpc.client.transport;

import io.nexusrpc.Experimental;
import java.time.Duration;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Experimental
public class GetOperationResultOptions {
  /** Create a builder for GetOperationResultOptions. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final Duration timeout;
  private final SortedMap<String, String> headers;

  private GetOperationResultOptions(Duration timeout, SortedMap<String, String> headers) {
    this.timeout = timeout;
    this.headers = headers;
  }

  /** Headers. The returned map operates without regard to case. */
  public Map<String, String> getHeaders() {
    return headers;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public static class Builder {
    private final SortedMap<String, String> headers;
    private Duration timeout;

    private Builder() {
      headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /** Get headers to mutate. */
    public Map<String, String> getHeaders() {
      return headers;
    }

    /**
     * Set the timeout for the operation.
     *
     * @param timeout the duration to wait for the operation result
     * @return this builder instance
     */
    public Builder setTimeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public GetOperationResultOptions build() {
      return new GetOperationResultOptions(timeout, headers);
    }
  }
}
