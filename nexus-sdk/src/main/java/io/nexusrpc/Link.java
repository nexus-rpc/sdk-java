package io.nexusrpc;

import java.net.URL;
import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Link link = (Link) o;
    return Objects.equals(url, link.url) && Objects.equals(type, link.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, type);
  }

  @Override
  public String toString() {
    return "Link{" + "url=" + url + ", type='" + type + '\'' + '}';
  }

  /** Builder for link. */
  public static class Builder {
    private @Nullable URL url;
    private @Nullable String type;

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
      Objects.requireNonNull(url, "URL required");
      Objects.requireNonNull(type, "Type required");
      return new Link(url, type);
    }
  }
}
