package io.nexusrpc.client;

import io.nexusrpc.*;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito.*;

public class ServiceClientTest {
  @Service
  interface TestService {
    @Operation
    int operation(int input);
  }

  @Test
  void test() throws OperationException {
    BiFunction<TestService, Integer, ?> f = TestService::operation;
    OperationDefinition operationDefinition =
        ServiceClient.extractOperationDefinition(TestService.class, f);
    System.out.println(operationDefinition);
  }
}
