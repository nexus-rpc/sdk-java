package io.nexusrpc.example;

import io.nexusrpc.Operation;
import io.nexusrpc.Service;
import java.util.List;
import java.util.Map;

public class TestServices {

  @Service
  public interface GenericService {
    @Operation
    String operation(String name);
  }

  @Service
  public interface VoidService {
    @Operation
    void operation();
  }

  @Service
  public interface IntService {
    @Operation
    int operation(int input);
  }

  @Service
  public interface IntegerService {
    @Operation
    Integer operation(Integer input);
  }

  @Service
  public interface GenericParameterService {
    @Operation
    Map<String, List<String>> operation(Map<String, List<String>> input);
  }
}
