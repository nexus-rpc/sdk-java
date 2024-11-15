package io.nexusrpc.example;

import io.nexusrpc.Operation;
import io.nexusrpc.Service;

public class TestServices {

  @Service
  public interface GenericService {
    @Operation
    String operation(String name);
  }
}
