package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.*;

import io.nexusrpc.Serializer;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HeaderTest {
  @Test
  void operationContextHeaders() {
    OperationContext context =
        OperationContext.newBuilder()
            .setOperation("operation")
            .setService("service")
            .putHeader("UPPER-CASE-HEADER", "UPPER-VALUE")
            .putHeader("lower-case-header", "lower-value")
            .build();
    // Verify that the map is case-insensitive
    verifyHeaders(context.getHeaders());
  }

  @Test
  void retryHandlerException() {
    // Verify that, by default, INTERNAL errors are retryable
    HandlerException he = new HandlerException(HandlerException.ErrorType.INTERNAL, "message");
    assertTrue(he.isRetryable());
    assertEquals(HandlerException.RetryBehavior.UNSPECIFIED, he.getRetryBehavior());
    // Verify that RetryBehavior.NON_RETRYABLE makes the error non-retryable
    he =
        new HandlerException(
            HandlerException.ErrorType.INTERNAL,
            "message",
            HandlerException.RetryBehavior.NON_RETRYABLE);
    assertFalse(he.isRetryable());
    assertEquals(HandlerException.RetryBehavior.NON_RETRYABLE, he.getRetryBehavior());
    // Verify that, by default, BAD_REQUEST errors are retryable
    he = new HandlerException(HandlerException.ErrorType.BAD_REQUEST, "message");
    assertFalse(he.isRetryable());
    assertEquals(HandlerException.RetryBehavior.UNSPECIFIED, he.getRetryBehavior());
    // Verify that RetryBehavior.RETRYABLE makes the error non-retryable
    he =
        new HandlerException(
            HandlerException.ErrorType.BAD_REQUEST,
            "message",
            HandlerException.RetryBehavior.RETRYABLE);
    assertTrue(he.isRetryable());
    assertEquals(HandlerException.RetryBehavior.RETRYABLE, he.getRetryBehavior());
  }

  @Test
  void handlerResultContentHeaders() {
    HandlerResultContent result =
        HandlerResultContent.newBuilder()
            .setData(new byte[] {})
            .putHeader("UPPER-CASE-HEADER", "UPPER-VALUE")
            .putHeader("lower-case-header", "lower-value")
            .build();
    // Verify that the map is case-insensitive
    verifyHeaders(result.getHeaders());
  }

  @Test
  void contentHeaders() {
    Serializer.Content content =
        Serializer.Content.newBuilder()
            .setData(new byte[] {})
            .putHeader("UPPER-CASE-HEADER", "UPPER-VALUE")
            .putHeader("lower-case-header", "lower-value")
            .build();
    // Verify that the map is case-insensitive
    verifyHeaders(content.getHeaders());
  }

  @Test
  void handlerInputHeaders() {
    HandlerInputContent content =
        HandlerInputContent.newBuilder()
            .setDataStream(new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8)))
            .putHeader("UPPER-CASE-HEADER", "UPPER-VALUE")
            .putHeader("lower-case-header", "lower-value")
            .build();
    // Verify that the map is case-insensitive
    verifyHeaders(content.getHeaders());
  }

  void verifyHeaders(Map<String, String> headers) {
    assertEquals("UPPER-VALUE", headers.get("upper-case-header"));
    assertEquals("UPPER-VALUE", headers.get("UPPER-CASE-HEADER"));
    assertEquals("lower-value", headers.get("lower-case-header"));
    assertEquals("lower-value", headers.get("LOWER-CASE-HEADER"));
    // Verify that all the keys are lower-case
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      assertEquals(entry.getKey().toLowerCase(), entry.getKey());
    }
  }
}
