package io.nexusrpc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class OperationExceptionTest {
  @Test
  void failureWithMessage() {
    OperationException ex = OperationException.failed("Test failure");

    assertEquals("Test failure", ex.getMessage());
    assertNull(ex.getCause());
    assertEquals(OperationState.FAILED, ex.getState());
  }

  @Test
  void failureWithCause() {
    RuntimeException cause = new RuntimeException("Root cause");
    OperationException ex = OperationException.failed(cause);

    assertEquals(cause.toString(), ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertEquals(OperationState.FAILED, ex.getState());
  }

  @Test
  void failureWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("Root cause");
    OperationException ex = OperationException.failed("Custom message", cause);

    assertEquals("Custom message", ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertEquals(OperationState.FAILED, ex.getState());
  }

  @Test
  void canceledWithMessage() {
    OperationException ex = OperationException.canceled("Test cancellation");

    assertEquals("Test cancellation", ex.getMessage());
    assertNull(ex.getCause());
    assertEquals(OperationState.CANCELED, ex.getState());
  }

  @Test
  void canceledWithCause() {
    RuntimeException cause = new RuntimeException("Cancellation reason");
    OperationException ex = OperationException.canceled(cause);

    assertEquals(cause.toString(), ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertEquals(OperationState.CANCELED, ex.getState());
  }

  @Test
  void canceledWithMessageAndCause() {
    RuntimeException cause = new RuntimeException("Cancellation reason");
    OperationException ex = OperationException.canceled("Custom cancellation message", cause);

    assertEquals("Custom cancellation message", ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertEquals(OperationState.CANCELED, ex.getState());
  }

  @Test
  void exceptionChaining() {
    Exception rootCause = new Exception("Root cause");
    RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
    OperationException ex = OperationException.failed("Operation failed", intermediateCause);

    assertEquals("Operation failed", ex.getMessage());
    assertEquals(intermediateCause, ex.getCause());
    assertEquals(rootCause, ex.getCause().getCause());
    assertEquals(OperationState.FAILED, ex.getState());
  }
}
