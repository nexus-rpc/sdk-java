package io.nexusrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method on a {@link ServiceImpl} as an implementation of an {@link Operation}.
 *
 * <p>The method with this annotation must be non-static, public, and have the same name as the
 * {@link Operation} annotated method in the service being represented. The method must accept no
 * arguments and return a {@link OperationHandler} with the first type variable as the operation
 * parameter type (or {@link Void} if none) and the second type variable as the operation return
 * type (or {@link Void} if void).
 *
 * <p>The method should not throw any exceptions and will only be called once to get the handler.
 * That handler will be reused for each operation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OperationImpl {}
