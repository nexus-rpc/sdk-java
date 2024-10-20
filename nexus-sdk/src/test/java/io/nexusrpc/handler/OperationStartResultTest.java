package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class OperationStartResultTest {
  @Test
  void nullSyncResult() {
    // Sync result can be null
    OperationStartResult<String> result = OperationStartResult.sync(null);
    assertTrue(result.isSync());
    assertEquals(null, result.getSyncResult());
  }

  @Test
  void nullSyncResultBuilder() {
    OperationStartResult<String> result =
        OperationStartResult.<String>newBuilder().setSyncResult(null).build();
    assertTrue(result.isSync());
    assertEquals(null, result.getSyncResult());
  }

  @Test
  void nullAsyncOperationIdFails() {
    // Async result cannot be null
    assertThrows(IllegalArgumentException.class, () -> OperationStartResult.async(null));
    assertThrows(IllegalArgumentException.class, () -> OperationStartResult.async(""));
  }

  @Test
  void nullAsyncOperationIdBuilderFails() {
    // Async result cannot be null
    assertThrows(
        IllegalArgumentException.class,
        () -> OperationStartResult.newBuilder().setAsyncOperationId(null));
    assertThrows(
        IllegalArgumentException.class,
        () -> OperationStartResult.newBuilder().setAsyncOperationId(""));
  }
}
