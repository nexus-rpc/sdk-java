package io.nexusrpc.handler;

/** Thrown when a service name or operation name is unrecognized. */
public class UnrecognizedOperationException extends Exception {
  public UnrecognizedOperationException(String service, String operation) {
    super("Unrecognized service " + service + " or operation " + operation);
  }
}
