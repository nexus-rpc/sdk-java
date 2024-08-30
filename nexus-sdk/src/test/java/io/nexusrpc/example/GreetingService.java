package io.nexusrpc.example;

import io.nexusrpc.Operation;
import io.nexusrpc.Service;

@Service
public interface GreetingService {
  @Operation
  String sayHello1(String name);

  @Operation
  String sayHello2(String name);
}
