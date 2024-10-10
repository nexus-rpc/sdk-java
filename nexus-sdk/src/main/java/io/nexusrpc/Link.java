package io.nexusrpc;

import java.net.URI;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Link contains a URI and a Type that can be used to decode the URI. Links can contain any
 * arbitrary information as a percent-encoded URI. It can be used to pass information about the
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

  private final URI uri;
  private final String type;

  private Link(URI uri, String type) {
    this.uri = uri;
    this.type = type;
  }

  /** URI information about the link. */
  public URI getUri() {
    return uri;
  }

  /** Type can describe an actual data type for decoding the URI. */
  public String getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Link link = (Link) o;
    return Objects.equals(uri, link.uri) && Objects.equals(type, link.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, type);
  }

  @Override
  public String toString() {
    return "Link{" + "uri=" + uri + ", type='" + type + '\'' + '}';
  }

  /** Builder for link. */
  public static class Builder {
    private @Nullable URI uri;
    private @Nullable String type;

    private Builder() {}

    private Builder(Link link) {
      uri = link.uri;
      type = link.type;
    }

    /** Set URI information about the link. It must be URL percent-encoded. */
    public Link.Builder setURI(URI uri) {
      this.uri = uri;
      return this;
    }

    /**
     * Type can describe an actual data type for decoding the URI. Valid chars: alphanumeric, '_',
     * '.', '/'
     */
    public Link.Builder setType(String type) {
      this.type = type;
      return this;
    }

    /** Build the link. */
    public Link build() {
      Objects.requireNonNull(uri, "URI required");
      Objects.requireNonNull(type, "Type required");
      return new Link(uri, type);
    }
  }
}
