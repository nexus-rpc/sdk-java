package io.nexusrpc.handler;

import io.nexusrpc.Link;
import java.util.*;
import org.jspecify.annotations.Nullable;

/** Details for handling operation start. */
public class OperationStartDetails {
  /** Create a builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder from an existing set of details. */
  public static Builder newBuilder(OperationStartDetails details) {
    return new Builder(details);
  }

  private final @Nullable String callbackUrl;
  private final Map<String, String> callbackHeaders;
  private final String requestId;
  private final List<Link> links;

  private OperationStartDetails(
      @Nullable String callbackUrl,
      Map<String, String> callbackHeaders,
      String requestId,
      List<Link> links) {
    this.callbackUrl = callbackUrl;
    this.callbackHeaders = callbackHeaders;
    this.requestId = requestId;
    this.links = links;
  }

  /**
   * Optional callback for asynchronous operations to deliver results to. If this is present and the
   * implementation is an asynchronous operation, the implementation should ensure this callback is
   * invoked with the result upon completion.
   */
  public @Nullable String getCallbackUrl() {
    return callbackUrl;
  }

  /** Headers to use on the callback if {@link #getCallbackUrl} is used. */
  public Map<String, String> getCallbackHeaders() {
    return callbackHeaders;
  }

  /** Unique request identifier from the caller to be used for deduplication. */
  public String getRequestId() {
    return requestId;
  }

  /**
   * Links contain arbitrary caller information. Handlers may use these links as metadata on
   * resources associated with and operation.
   */
  public List<Link> getLinks() {
    return links;
  }

  /** Builder for operation start details. */
  public static class Builder {
    private @Nullable String callbackUrl;
    private final Map<String, String> callbackHeaders;
    private @Nullable String requestId;
    private List<Link> links;

    private Builder() {
      callbackHeaders = new HashMap<>();
      links = new ArrayList<>();
    }

    private Builder(OperationStartDetails details) {
      callbackUrl = details.callbackUrl;
      callbackHeaders = new HashMap<>(details.callbackHeaders);
      requestId = details.requestId;
      links = details.links;
    }

    /** Set callback URL. */
    public Builder setCallbackUrl(String callbackUrl) {
      this.callbackUrl = callbackUrl;
      return this;
    }

    /** Get callback headers for mutation. */
    public Map<String, String> getCallbackHeaders() {
      return callbackHeaders;
    }

    /** Put a single callback header. */
    public Builder putCallbackHeader(String key, String value) {
      callbackHeaders.put(key, value);
      return this;
    }

    /** Set request ID. Required. */
    public Builder setRequestId(String requestId) {
      this.requestId = requestId;
      return this;
    }

    /** Add a link. */
    public Builder addLinks(Link link) {
      this.links.add(link);
      return this;
    }

    /** Build the details. */
    public OperationStartDetails build() {
      Objects.requireNonNull(requestId, "Request ID required");
      return new OperationStartDetails(
          callbackUrl,
          Collections.unmodifiableMap(new HashMap<>(callbackHeaders)),
          requestId,
          Collections.unmodifiableList(new ArrayList<>(links)));
    }
  }
}
