package io.nexusrpc.example;

import java.util.concurrent.Future;

public interface ApiClient {
  Future<String> createGreeting(String name);
}
