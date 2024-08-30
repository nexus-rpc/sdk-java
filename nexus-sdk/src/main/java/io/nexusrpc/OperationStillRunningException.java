package io.nexusrpc;

/** An operation result was requested, but it is still running. */
public class OperationStillRunningException extends Exception {
  public OperationStillRunningException() {
    super("Operation still running");
  }
}
