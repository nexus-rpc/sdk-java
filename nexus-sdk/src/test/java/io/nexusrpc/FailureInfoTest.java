package io.nexusrpc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FailureInfoTest {
  @Test
  void builderWithAllFields() {
    FailureInfo failure =
        FailureInfo.newBuilder()
            .setMessage("Test failure message")
            .setStackTrace("at Test.method(Test.java:10)")
            .putMetadata("key1", "value1")
            .putMetadata("key2", "value2")
            .setDetailsJson("{\"detail\": \"value\"}")
            .build();

    assertEquals("Test failure message", failure.getMessage());
    assertEquals("at Test.method(Test.java:10)", failure.getStackTrace());
    assertEquals(2, failure.getMetadata().size());
    assertEquals("value1", failure.getMetadata().get("key1"));
    assertEquals("value2", failure.getMetadata().get("key2"));
    assertEquals("{\"detail\": \"value\"}", failure.getDetailsJson());
  }

  @Test
  void builderWithNullStackTrace() {
    FailureInfo failure =
        FailureInfo.newBuilder().setMessage("Test failure message").setStackTrace(null).build();

    assertEquals("Test failure message", failure.getMessage());
    assertNull(failure.getStackTrace());
  }

  @Test
  void builderWithoutStackTrace() {
    FailureInfo failure = FailureInfo.newBuilder().setMessage("Test failure message").build();

    assertEquals("Test failure message", failure.getMessage());
    assertNull(failure.getStackTrace());
  }

  @Test
  void builderRequiresMessage() {
    assertThrows(NullPointerException.class, () -> FailureInfo.newBuilder().build());
  }

  @Test
  void builderFromExistingFailure() {
    FailureInfo original =
        FailureInfo.newBuilder()
            .setMessage("Original message")
            .setStackTrace("Original stack trace")
            .putMetadata("key", "value")
            .setDetailsJson("{\"original\": true}")
            .build();

    FailureInfo copied =
        FailureInfo.newBuilder(original)
            .setMessage("Updated message")
            .putMetadata("newKey", "newValue")
            .build();

    assertEquals("Updated message", copied.getMessage());
    assertEquals("Original stack trace", copied.getStackTrace());
    assertEquals(2, copied.getMetadata().size());
    assertEquals("value", copied.getMetadata().get("key"));
    assertEquals("newValue", copied.getMetadata().get("newKey"));
    assertEquals("{\"original\": true}", copied.getDetailsJson());
  }

  @Test
  void metadataIsImmutable() {
    FailureInfo failure =
        FailureInfo.newBuilder().setMessage("Test").putMetadata("key", "value").build();

    assertThrows(
        UnsupportedOperationException.class, () -> failure.getMetadata().put("new", "value"));
  }

  @Test
  void equalsAndHashCode() {
    FailureInfo failure1 =
        FailureInfo.newBuilder()
            .setMessage("message")
            .setStackTrace("stack")
            .putMetadata("key", "value")
            .setDetailsJson("{}")
            .build();

    FailureInfo failure2 =
        FailureInfo.newBuilder()
            .setMessage("message")
            .setStackTrace("stack")
            .putMetadata("key", "value")
            .setDetailsJson("{}")
            .build();

    FailureInfo failure3 =
        FailureInfo.newBuilder()
            .setMessage("different")
            .setStackTrace("stack")
            .putMetadata("key", "value")
            .build();

    assertEquals(failure1, failure2);
    assertEquals(failure1.hashCode(), failure2.hashCode());
    assertNotEquals(failure1, failure3);
    assertNotEquals(failure1.hashCode(), failure3.hashCode());
  }

  @Test
  void toStringContainsAllFields() {
    FailureInfo failure =
        FailureInfo.newBuilder()
            .setMessage("test message")
            .setStackTrace("test stack")
            .putMetadata("key", "value")
            .setDetailsJson("{}")
            .build();

    String str = failure.toString();
    assertTrue(str.contains("test message"));
    assertTrue(str.contains("test stack"));
    assertTrue(str.contains("key"));
    assertTrue(str.contains("value"));
    assertTrue(str.contains("{}"));
  }
}
