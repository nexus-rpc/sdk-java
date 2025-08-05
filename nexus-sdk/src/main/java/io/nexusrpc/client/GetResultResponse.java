package io.nexusrpc.client;

import io.nexusrpc.Link;
import java.util.List;

public class GetResultResponse<T> {
  public static Builder newBuilder() {
    return new Builder();
  }

  private final T result;
  private final List<Link> links;

  private GetResultResponse(T result, List<Link> links) {
    this.result = result;
    this.links = links;
  }

  public T getResult() {
    return result;
  }

  public List<Link> getLinks() {
    return links;
  }

  public static class Builder<T> {
    private T result;
    private List<Link> links;

    public Builder setResult(T result) {
      this.result = result;
      return this;
    }

    public Builder setLinks(List<Link> links) {
      this.links = links;
      return this;
    }

    public GetResultResponse<T> build() {
      return new GetResultResponse<>(result, links);
    }
  }
}
