package io.nexusrpc;

/** An operation was not found for an ID. */
public class OperationNotFoundException extends Exception {
  public OperationNotFoundException() {
    super("Operation not found");
  }
}
