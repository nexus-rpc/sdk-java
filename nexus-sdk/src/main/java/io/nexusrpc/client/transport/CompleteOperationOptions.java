package io.nexusrpc.client.transport;

import io.nexusrpc.Experimental;
import io.nexusrpc.Link;
import io.nexusrpc.OperationException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Experimental
public class CompleteOperationOptions {
  public static Builder newBuilder() {
    return new Builder();
  }

  private final Map<String, String> headers;
  private final Object result;
  private final OperationException error;
  private final String operationToken;
  private final Instant startTime;
  private final List<Link> links;

  private CompleteOperationOptions(
      Map<String, String> headers,
      Object result,
      OperationException error,
      String operationToken,
      Instant startTime,
      List<Link> links) {
    this.headers = headers;
    this.result = result;
    this.error = error;
    this.operationToken = operationToken;
    this.startTime = startTime;
    this.links = links;
  }

  public String getOperationToken() {
    return operationToken;
  }

  public List<Link> getLinks() {
    return links;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public OperationException getError() {
    return error;
  }

  public Object getResult() {
    return result;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public static class Builder {
    private Map<String, String> headers;
    private Object result;
    private OperationException error;
    private String operationToken;
    private Instant startTime;
    private List<Link> links;

    public Builder setHeaders(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public Builder setResult(Object result) {
      this.result = result;
      return this;
    }

    public Builder setError(OperationException error) {
      this.error = error;
      return this;
    }

    public Builder setOperationToken(String operationToken) {
      this.operationToken = operationToken;
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
      return new CompleteOperationOptions(headers, result, error, operationToken, startTime, links);
    }
  }
}
