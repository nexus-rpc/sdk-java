package io.nexusrpc.client.transport;

import io.nexusrpc.Link;
import java.util.List;

public class GetOperationResultResponse {
  public static GetOperationResultResponse.Builder newBuilder() {
    return new GetOperationResultResponse.Builder();
  }

  private final Object result;
  private final List<Link> links;

  private GetOperationResultResponse(Object result, List<Link> links) {
    this.result = result;
    this.links = links;
  }

  public Object getResult() {
    return result;
  }

  public List<Link> getLinks() {
    return links;
  }

  public static class Builder {
    private Object result;
    private List<Link> links;

    public GetOperationResultResponse.Builder setResult(Object result) {
      this.result = result;
      return this;
    }

    public GetOperationResultResponse.Builder setLinks(List<Link> links) {
      this.links = links;
      return this;
    }

    public GetOperationResultResponse build() {
      return new GetOperationResultResponse(result, links);
    }
  }
}
