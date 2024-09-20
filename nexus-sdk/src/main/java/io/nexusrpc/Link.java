package io.nexusrpc;

import java.net.URL;
import org.jspecify.annotations.Nullable;

/**
 * Link contains a URL and a Type that can be used to decode the URL. Links can contain any
 * arbitrary information as a percent-encoded URL. It can be used to pass information about the
 * caller to the handler, or vice-versa.
 */
public class Link {
  /** Create a builder. */
  public static Link.Builder newBuilder() {
    return new Link.Builder();
  }

  /** Create a builder from an existing Link. */
  public static Link.Builder newBuilder(Link info) {
    return new Link.Builder(info);
  }

  private final URL url;
  private final String type;

  private Link(URL url, String type) {
    this.url = url;
    this.type = type;
  }

  /** URL information about the link. */
  public URL getUrl() {
    return url;
  }

  /** Type can describe an actual data type for decoding the URL. */
  public String getType() {
    return type;
  }

  /** Builder for link. */
  public static class Builder {
    @Nullable private URL url;
    @Nullable private String type;

    private Builder() {}

    private Builder(Link link) {
      url = link.url;
      type = link.type;
    }

    /** Set URL information about the link. It must be URL percent-encoded. */
    public Link.Builder setUrl(URL url) {
      this.url = url;
      return this;
    }

    /**
     * Type can describe an actual data type for decoding the URL. Valid chars: alphanumeric, '_',
     * '.', '/'
     */
    public Link.Builder setType(String type) {
      this.type = type;
      return this;
    }

    /** Build the link. */
    public Link build() {
      return new Link(url, type);
    }
  }
}
