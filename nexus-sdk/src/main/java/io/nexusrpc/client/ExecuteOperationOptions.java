package io.nexusrpc.client;

import io.nexusrpc.Link;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ExecuteOperationOptions {
  private final Map<String, String> headers;
  private final String callbackURL;
  private final Map<String, String> callbackHeaders;
  private final String requestId;
  private final List<Link> inboundLinks;
  private final Duration timeout;

  public ExecuteOperationOptions(
      Map<String, String> headers,
      String callbackURL,
      Map<String, String> callbackHeaders,
      String requestId,
      List<Link> inboundLinks,
      Duration timeout) {
    this.headers = headers;
    this.callbackURL = callbackURL;
    this.callbackHeaders = callbackHeaders;
    this.requestId = requestId;
    this.inboundLinks = inboundLinks;
    this.timeout = timeout;
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

  public Duration getTimeout() {
    return timeout;
  }

  public static class Builder {
    private final SortedMap<String, String> headers;
    private String callbackURL;
    private SortedMap<String, String> callbackHeaders;
    private String requestId;
    private List<Link> inboundLinks;
    private Duration timeout;

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

    public Builder setTimeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public ExecuteOperationOptions build() {
      return new ExecuteOperationOptions(
          headers, callbackURL, callbackHeaders, requestId, inboundLinks, timeout);
    }
  }
}
