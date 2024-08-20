package io.nexusrpc;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import org.junit.jupiter.api.Test;

public class ServiceDefinitionTest {

  interface InvalidServiceNoAnnotation {}

  @Service
  static class InvalidServiceAsClass {}

  @Service
  interface InvalidSuperService {}

  @Service
  interface InvalidSubService extends InvalidSuperService {}

  @Service(name = "invalid-service-with-operations")
  interface InvalidSuperServiceWithOperations {
    void superMethodWithNoAnnotation();

    @Operation(name = "super-method-different-name1")
    void superMethodDifferentName();

    @Operation
    Object covariantReturn();
  }

  @Service(name = "invalid-service-with-operations")
  interface InvalidServiceWithOperations extends InvalidSuperServiceWithOperations {
    void methodWithNoAnnotation();

    @Operation
    void superMethodWithNoAnnotation();

    @Operation
    void twoParameterOperation(String one, String two);

    @Operation
    <T> void genericOperation(T param);

    @Operation
    void hasThrowsClause() throws Exception;

    @Operation
    default void hasDefault() {}

    @Operation
    static void staticOperation() {}

    @Operation(name = "super-method-different-name2")
    void superMethodDifferentName();

    @Operation
    String covariantReturn();
  }

  @Service
  interface InvalidServiceDuplicateOperation {
    @Operation
    void duplicateWhenNameOverridden1();

    @Operation(name = "duplicateWhenNameOverridden1")
    void duplicateWhenNameOverridden2();
  }

  private static void assertInvalidService(Class<?> clazz, String... expectedFailures) {
    try {
      ServiceDefinition.fromClass(clazz);
      fail("Expected failures, none found");
    } catch (Exception e) {
      for (String expectedFailure : expectedFailures) {
        assertTrue(
            e.getMessage().contains(expectedFailure),
            "Expected '"
                + expectedFailure
                + "' to appear in the exception message of: "
                + e.getMessage());
      }
    }
  }

  private static String expectedOperationFailure(String method, String message) {
    return expectedOperationFailure(method, InvalidServiceWithOperations.class, message);
  }

  private static String expectedOperationFailure(String method, Class<?> clazz, String message) {
    return method + " on " + clazz.getName() + " is invalid: " + message;
  }

  @Test
  void invalidService() {
    assertInvalidService(InvalidServiceNoAnnotation.class, "Missing @Service annotation");
    assertInvalidService(InvalidServiceAsClass.class, "Must be an interface");
    assertInvalidService(
        InvalidSubService.class,
        "InvalidSuperService has a service annotation whose name (InvalidSuperService) "
            + "does not match the expected name on the final interface (InvalidSubService)");
    assertInvalidService(
        InvalidServiceWithOperations.class,
        expectedOperationFailure("methodWithNoAnnotation", "Missing @Operation annotation"),
        expectedOperationFailure(
            "superMethodWithNoAnnotation",
            InvalidSuperServiceWithOperations.class,
            "Missing @Operation annotation"),
        expectedOperationFailure("twoParameterOperation", "Can have no more than one parameter"),
        expectedOperationFailure("genericOperation", "Cannot be generic"),
        expectedOperationFailure("hasThrowsClause", "Cannot have throws clause"),
        expectedOperationFailure("hasDefault", "Cannot have default implementation"),
        expectedOperationFailure("staticOperation", "Cannot be static"),
        "superMethodDifferentName on "
            + InvalidSuperServiceWithOperations.class.getName()
            + " mismatches against another operation of the same name/signature",
        "covariantReturn on "
            + InvalidServiceWithOperations.class.getName()
            + " mismatches against another operation of the same name/signature");
    assertInvalidService(
        InvalidServiceDuplicateOperation.class,
        "Multiple operations named 'duplicateWhenNameOverridden1'");
  }

  @Service(name = "ValidServiceWithOperations")
  interface ValidSuperServiceWithOperations {
    @Operation
    void superMethod();

    @Operation
    void superInterfaceOnly();
  }

  @Service
  interface ValidServiceWithOperations extends ValidSuperServiceWithOperations {
    @Operation
    void superMethod();

    @Operation
    void noParamNoReturn();

    @Operation
    String noParamSingleReturn();

    @Operation
    void singleParamNoReturn(String param);

    @Operation
    String singleParamSingleReturn(String param);

    @Operation(name = "custom-name")
    void customName();
  }

  private static void assertOperationExists(
      ServiceDefinition defn, String name, Type outputType, Type inputType) {
    OperationDefinition operation = defn.getOperations().get(name);
    assertNotNull(operation);
    assertEquals(name, operation.getName());
    assertEquals(outputType, operation.getOutputType());
    assertEquals(inputType, operation.getInputType());
  }

  @Test
  void validService() {
    ServiceDefinition defn = ServiceDefinition.fromClass(ValidServiceWithOperations.class);
    assertOperationExists(defn, "superMethod", Void.TYPE, Void.TYPE);
    assertOperationExists(defn, "noParamNoReturn", Void.TYPE, Void.TYPE);
    assertOperationExists(defn, "noParamSingleReturn", String.class, Void.TYPE);
    assertOperationExists(defn, "singleParamNoReturn", Void.TYPE, String.class);
    assertOperationExists(defn, "singleParamSingleReturn", String.class, String.class);
    assertOperationExists(defn, "custom-name", Void.TYPE, Void.TYPE);
  }
}
