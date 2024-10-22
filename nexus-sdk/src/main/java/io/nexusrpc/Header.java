package io.nexusrpc;

/** Collection of well-known headers for Nexus. */
public class Header {
  /** Header for the total time to complete a Nexus request. */
  public static final String REQUEST_TIMEOUT = "Request-Timeout";

  /**
   * Total time to complete a Nexus operation. Unlike {@link Header#REQUEST_TIMEOUT}, this applies
   * to the whole operation, not just a single HTTP request.
   */
  public static final String OPERATION_TIMEOUT = "Operation-Timeout";

  private Header() {}
}
