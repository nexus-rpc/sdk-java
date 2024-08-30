package io.nexusrpc;

/** State an operation can be in. */
public enum OperationState {
  /** Indicates an operation is started and not yet completed. */
  RUNNING,
  /** Indicates an operation completed successfully. */
  SUCCEEDED,
  /** Indicates an operation completed as failed. */
  FAILED,
  /** Indicates an operation completed as canceled. */
  CANCELLED,
}
