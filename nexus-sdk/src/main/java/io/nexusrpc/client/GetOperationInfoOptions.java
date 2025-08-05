package io.nexusrpc.client;

import io.nexusrpc.Experimental;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * GetOperationInfoOptions represents options for getting information about an operation.
 *
 * <p>These options can be used with {@link OperationHandle#getInfo(GetOperationInfoOptions)}.
 */
@Experimental
public class GetOperationInfoOptions {
  /** Create a builder for GetOperationInfoOptions. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final SortedMap<String, String> headers;

  private GetOperationInfoOptions(SortedMap<String, String> headers) {
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

    public GetOperationInfoOptions build() {
      return new GetOperationInfoOptions(headers);
    }
  }
}
