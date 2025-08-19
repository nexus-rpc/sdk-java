package io.nexusrpc.client;

import io.nexusrpc.Link;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/** CompleteOperationOptions are options for the CompleteOperation client and server APIs. */
public class CompleteOperationOptions {
  public static Builder newBuilder() {
    return new Builder();
  }

  private final Map<String, String> headers;
  private final Instant startTime;
  private final List<Link> links;

  private CompleteOperationOptions(
      Map<String, String> headers, Instant startTime, List<Link> links) {
    this.headers = headers;
    this.startTime = startTime;
    this.links = links;
  }

  public List<Link> getLinks() {
    return links;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public static class Builder {
    private Map<String, String> headers;
    private Instant startTime;
    private List<Link> links;

    public Builder setHeaders(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public Builder setStartTime(Instant startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder setLinks(List<Link> links) {
      this.links = links;
      return this;
    }

    public CompleteOperationOptions build() {
      return new CompleteOperationOptions(headers, startTime, links);
    }
  }
}
