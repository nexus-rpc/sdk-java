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

  /**
   * Header for the unique ID returned by the StartOperation response for async operations. Must be
   * set on callback headers to support completing operations before the start response is received.
   */
  public static final String HEADER_OPERATION_ID = "Nexus-Operation-Id";

  private Header() {}
}
