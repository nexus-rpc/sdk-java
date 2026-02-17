package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.*;

import io.nexusrpc.FailureInfo;
import org.junit.jupiter.api.Test;

public class HandlerExceptionTest {
  @Test
  void constructorWithErrorTypeAndMessage() {
    @SuppressWarnings("deprecation")
    HandlerException ex =
        new HandlerException(HandlerException.ErrorType.BAD_REQUEST, "Invalid input");

    assertEquals("Invalid input", ex.getCause().getMessage());
    assertEquals(HandlerException.ErrorType.BAD_REQUEST, ex.getErrorType());
    assertEquals(HandlerException.RetryBehavior.UNSPECIFIED, ex.getRetryBehavior());
    assertNull(ex.getOriginalFailure());
  }

  @SuppressWarnings("deprecation")
  @Test
  void constructorWithErrorTypeMessageAndRetryBehavior() {
    HandlerException ex =
        new HandlerException(
            HandlerException.ErrorType.INTERNAL,
            "Server error",
            HandlerException.RetryBehavior.NON_RETRYABLE);

    assertEquals("Server error", ex.getCause().getMessage());
    assertEquals(HandlerException.ErrorType.INTERNAL, ex.getErrorType());
    assertEquals(HandlerException.RetryBehavior.NON_RETRYABLE, ex.getRetryBehavior());
    assertFalse(ex.isRetryable());
  }

  @Test
  void constructorWithErrorTypeMessageAndCause() {
    RuntimeException cause = new RuntimeException("Root cause");
    HandlerException ex =
        new HandlerException(HandlerException.ErrorType.INTERNAL, "Handler error", cause);

    assertEquals("Handler error", ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertEquals(HandlerException.ErrorType.INTERNAL, ex.getErrorType());
    assertEquals(HandlerException.RetryBehavior.UNSPECIFIED, ex.getRetryBehavior());
  }

  @Test
  void constructorWithErrorTypeAndCause() {
    RuntimeException cause = new RuntimeException("Root cause");
    HandlerException ex = new HandlerException(HandlerException.ErrorType.UNAVAILABLE, cause);

    assertEquals("handler error: Root cause", ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertEquals(HandlerException.ErrorType.UNAVAILABLE, ex.getErrorType());
    assertTrue(ex.isRetryable());
  }

  @Test
  void constructorWithErrorTypeAndNullCause() {
    HandlerException ex =
        new HandlerException(HandlerException.ErrorType.NOT_FOUND, (Throwable) null);

    assertEquals("handler error", ex.getMessage());
    assertNull(ex.getCause());
    assertEquals(HandlerException.ErrorType.NOT_FOUND, ex.getErrorType());
    assertFalse(ex.isRetryable());
  }

  @Test
  void constructorWithErrorTypeCauseAndRetryBehavior() {
    RuntimeException cause = new RuntimeException("Error");
    HandlerException ex =
        new HandlerException(
            HandlerException.ErrorType.BAD_REQUEST,
            cause,
            HandlerException.RetryBehavior.RETRYABLE);

    assertEquals("handler error: Error", ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertTrue(ex.isRetryable());
  }

  @Test
  void constructorWithAllParameters() {
    RuntimeException cause = new RuntimeException("Cause");
    FailureInfo originalFailure =
        FailureInfo.newBuilder()
            .setMessage("Original failure")
            .setStackTrace("at Test.method(Test.java:1)")
            .build();

    HandlerException ex =
        new HandlerException(
            HandlerException.ErrorType.INTERNAL,
            "Custom message",
            cause,
            HandlerException.RetryBehavior.NON_RETRYABLE,
            originalFailure);

    assertEquals("Custom message", ex.getMessage());
    assertEquals(cause, ex.getCause());
    assertEquals(HandlerException.ErrorType.INTERNAL, ex.getErrorType());
    assertEquals(HandlerException.RetryBehavior.NON_RETRYABLE, ex.getRetryBehavior());
    assertEquals(originalFailure, ex.getOriginalFailure());
    assertFalse(ex.isRetryable());
  }

  @Test
  void constructorWithRawErrorType() {
    HandlerException ex =
        new HandlerException(
            "CUSTOM_ERROR_TYPE",
            new RuntimeException("Error"),
            HandlerException.RetryBehavior.UNSPECIFIED);

    assertEquals("CUSTOM_ERROR_TYPE", ex.getRawErrorType());
    assertEquals(HandlerException.ErrorType.UNKNOWN, ex.getErrorType());
    assertTrue(ex.isRetryable());
  }

  @Test
  void constructorWithRawErrorTypeAndMessage() {
    RuntimeException cause = new RuntimeException("Cause");
    HandlerException ex =
        new HandlerException(
            "CUSTOM_TYPE", "Custom message", cause, HandlerException.RetryBehavior.RETRYABLE);

    assertEquals("CUSTOM_TYPE", ex.getRawErrorType());
    assertEquals(HandlerException.ErrorType.UNKNOWN, ex.getErrorType());
    assertEquals("Custom message", ex.getMessage());
    assertTrue(ex.isRetryable());
  }

  @Test
  void constructorWithRawErrorTypeAndOriginalFailure() {
    RuntimeException cause = new RuntimeException("Error");
    FailureInfo originalFailure =
        FailureInfo.newBuilder().setMessage("Original").setStackTrace("stack").build();

    HandlerException ex =
        new HandlerException(
            "MY_ERROR",
            "Message",
            cause,
            HandlerException.RetryBehavior.UNSPECIFIED,
            originalFailure);

    assertEquals("MY_ERROR", ex.getRawErrorType());
    assertEquals(originalFailure, ex.getOriginalFailure());
    assertEquals("Original", ex.getOriginalFailure().getMessage());
  }

  @Test
  void retryBehaviorOverridesDefaultForRetryableError() {
    // INTERNAL is retryable by default
    @SuppressWarnings("deprecation")
    HandlerException ex =
        new HandlerException(
            HandlerException.ErrorType.INTERNAL,
            "Error",
            HandlerException.RetryBehavior.NON_RETRYABLE);
    assertFalse(ex.isRetryable());
  }

  @Test
  void retryBehaviorOverridesDefaultForNonRetryableError() {
    // BAD_REQUEST is non-retryable by default
    @SuppressWarnings("deprecation")
    HandlerException ex =
        new HandlerException(
            HandlerException.ErrorType.BAD_REQUEST,
            "Error",
            HandlerException.RetryBehavior.RETRYABLE);
    assertTrue(ex.isRetryable());
  }

  @Test
  void defaultRetryBehaviorForRetryableErrors() {
    HandlerException.ErrorType[] retryableTypes = {
      HandlerException.ErrorType.RESOURCE_EXHAUSTED,
      HandlerException.ErrorType.INTERNAL,
      HandlerException.ErrorType.UNAVAILABLE,
      HandlerException.ErrorType.UPSTREAM_TIMEOUT,
      HandlerException.ErrorType.UNKNOWN
    };

    for (HandlerException.ErrorType type : retryableTypes) {
      HandlerException ex = new HandlerException(type, new RuntimeException("error"));
      assertTrue(ex.isRetryable(), type + " should be retryable by default");
    }
  }

  @Test
  void defaultRetryBehaviorForNonRetryableErrors() {
    HandlerException.ErrorType[] nonRetryableTypes = {
      HandlerException.ErrorType.BAD_REQUEST,
      HandlerException.ErrorType.UNAUTHENTICATED,
      HandlerException.ErrorType.UNAUTHORIZED,
      HandlerException.ErrorType.NOT_FOUND,
      HandlerException.ErrorType.NOT_IMPLEMENTED
    };

    for (HandlerException.ErrorType type : nonRetryableTypes) {
      HandlerException ex = new HandlerException(type, new RuntimeException("error"));
      assertFalse(ex.isRetryable(), type + " should not be retryable by default");
    }
  }

  @Test
  void unknownRawErrorTypeDefaultsToUnknownEnum() {
    HandlerException ex =
        new HandlerException(
            "COMPLETELY_UNKNOWN_ERROR_TYPE",
            new RuntimeException("error"),
            HandlerException.RetryBehavior.UNSPECIFIED);

    assertEquals("COMPLETELY_UNKNOWN_ERROR_TYPE", ex.getRawErrorType());
    assertEquals(HandlerException.ErrorType.UNKNOWN, ex.getErrorType());
  }

  @Test
  void knownRawErrorTypeMatchesEnum() {
    HandlerException ex =
        new HandlerException(
            "BAD_REQUEST",
            new RuntimeException("error"),
            HandlerException.RetryBehavior.UNSPECIFIED);

    assertEquals("BAD_REQUEST", ex.getRawErrorType());
    assertEquals(HandlerException.ErrorType.BAD_REQUEST, ex.getErrorType());
  }
}
