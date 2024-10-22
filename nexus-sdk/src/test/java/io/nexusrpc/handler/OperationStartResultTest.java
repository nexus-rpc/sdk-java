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
    String syncResult = null;
    OperationStartResult<String> result = OperationStartResult.newSyncBuilder(syncResult).build();
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
    assertThrows(IllegalArgumentException.class, () -> OperationStartResult.newAsyncBuilder(null));
    assertThrows(IllegalArgumentException.class, () -> OperationStartResult.newAsyncBuilder(""));
  }
}
