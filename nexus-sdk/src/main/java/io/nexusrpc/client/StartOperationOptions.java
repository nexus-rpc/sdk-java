package io.nexusrpc.client;

import io.nexusrpc.Experimental;
import io.nexusrpc.Link;
import java.util.*;

/** Start operation options for {@link ServiceClient#startOperation}. */
@Experimental
public class StartOperationOptions {
  /** Create a builder for start operation options. */
  public static Builder newBuilder() {
    return new Builder();
  }

  private final Map<String, String> headers;
  private final String callbackURL;
  private final Map<String, String> callbackHeaders;
  private final String requestId;
  private final List<Link> inboundLinks;

  private StartOperationOptions(
      Map<String, String> headers,
      String callbackURL,
      Map<String, String> callbackHeaders,
      String requestId,
      List<Link> inboundLinks) {
    this.headers = headers;
    this.callbackURL = callbackURL;
    this.callbackHeaders = callbackHeaders;
    this.requestId = requestId;
    this.inboundLinks = inboundLinks;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getCallbackURL() {
    return callbackURL;
  }

  public Map<String, String> getCallbackHeaders() {
    return callbackHeaders;
  }

  public String getRequestId() {
    return requestId;
  }

  public List<Link> getInboundLinks() {
    return inboundLinks;
  }

  public static class Builder {
    private final SortedMap<String, String> headers;
    private String callbackURL;
    private SortedMap<String, String> callbackHeaders;
    private String requestId;
    private List<Link> inboundLinks;

    public Builder() {
      headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      callbackHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    public Builder setCallbackURL(String callbackURL) {
      this.callbackURL = callbackURL;
      return this;
    }

    public Map<String, String> getCallbackHeaders() {
      return callbackHeaders;
    }

    public Builder setRequestId(String requestId) {
      this.requestId = requestId;
      return this;
    }

    public Builder setInboundLinks(List<Link> inboundLinks) {
      this.inboundLinks = inboundLinks;
      return this;
    }

    public Builder putHeader(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    public StartOperationOptions build() {
      return new StartOperationOptions(
          headers,
          callbackURL,
          callbackHeaders,
          requestId,
          inboundLinks != null ? inboundLinks : Collections.emptyList());
    }
  }
}
