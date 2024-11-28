package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
