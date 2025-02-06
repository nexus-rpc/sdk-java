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
   *
   * @deprecated Use {@link Header#OPERATION_TOKEN} instead.
   */
  @Deprecated public static final String OPERATION_ID = "Nexus-Operation-Id";

  /**
   * Header for the unique token returned by the StartOperation response for async operations. Must
   * be set on callback headers to support completing operations before the start response is
   * received.
   */
  public static final String OPERATION_TOKEN = "Nexus-Operation-Token";

  /**
   * Header for to set time the operation started. Used when a completion request is received before
   * a started response. Should be in a valid HTTP/1.1 format per
   * https://www.rfc-editor.org/rfc/rfc5322.html#section-3.3. If is omitted, the time the completion
   * is received will be used as operation start time.
   */
  public static final String OPERATION_START_TIME = "Nexus-Operation-Start-Time";

  private Header() {}
}
