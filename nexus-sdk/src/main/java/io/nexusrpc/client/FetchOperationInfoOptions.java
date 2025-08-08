package io.nexusrpc.client;

import io.nexusrpc.Experimental;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * FetchOperationInfoOptions represents options for getting information about an operation.
 *
 * <p>These options can be used with {@link OperationHandle#getInfo(FetchOperationInfoOptions)}.
 */
@Experimental
public class FetchOperationInfoOptions {
  /** Create a builder for FetchOperationInfoOptions. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final SortedMap<String, String> headers;

  private FetchOperationInfoOptions(SortedMap<String, String> headers) {
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

    public FetchOperationInfoOptions build() {
      return new FetchOperationInfoOptions(headers);
    }
  }
}
