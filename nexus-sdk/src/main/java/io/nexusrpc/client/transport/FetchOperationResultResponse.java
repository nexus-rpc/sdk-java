package io.nexusrpc.client.transport;

import io.nexusrpc.Experimental;
import io.nexusrpc.Link;
import io.nexusrpc.Serializer;
import java.util.List;

@Experimental
public class FetchOperationResultResponse {
  public static Builder newBuilder() {
    return new Builder();
  }

  private final Serializer.Content result;
  private final List<Link> links;

  private FetchOperationResultResponse(Serializer.Content result, List<Link> links) {
    this.result = result;
    this.links = links;
  }

  public Serializer.Content getResult() {
    return result;
  }

  public List<Link> getLinks() {
    return links;
  }

  public static class Builder {
    private Serializer.Content result;
    private List<Link> links;

    public Builder setResult(Serializer.Content result) {
      this.result = result;
      return this;
    }

    public Builder setLinks(List<Link> links) {
      this.links = links;
      return this;
    }

    public FetchOperationResultResponse build() {
      return new FetchOperationResultResponse(result, links);
    }
  }
}
