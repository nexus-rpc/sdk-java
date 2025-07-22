package io.nexusrpc.client.transport;

import io.nexusrpc.Link;
import java.util.List;

public class StartOperationResponse {
  public static Builder newBuilder() {
    return new Builder();
  }

  private final Object syncResult;
  private final List<Link> links;
  private final String asyncOperationToken;

  private StartOperationResponse(Object syncResult, String asyncOperationToken, List<Link> links) {
    this.syncResult = syncResult;
    this.asyncOperationToken = asyncOperationToken;
    this.links = links;
  }

  public Object getSyncResult() {
    return syncResult;
  }

  public String getAsyncOperationToken() {
    return asyncOperationToken;
  }

  public List<Link> getLinks() {
    return links;
  }

  public static class Builder {
    private Object syncResult;
    private String asyncOperationToken;
    private List<Link> links;

    public Builder setResult(Object syncResult) {
      this.syncResult = syncResult;
      return this;
    }

    public Builder setAsyncOperationToken(String asyncOperationToken) {
      this.asyncOperationToken = asyncOperationToken;
      return this;
    }

    public Builder setLinks(List<Link> links) {
      this.links = links;
      return this;
    }

    public StartOperationResponse build() {
      return new StartOperationResponse(syncResult, asyncOperationToken, links);
    }
  }
}
