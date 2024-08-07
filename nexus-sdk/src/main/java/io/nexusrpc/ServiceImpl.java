package io.nexusrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an implementation of a {@link Service} interface.
 *
 * <p>For every method in {@link #service}, this class must have an equivalent non-static, public
 * method annotated with {@link OperationImpl}. See that annotation's documentation for more
 * details.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceImpl {
  /** The {@link Service}-annotated interface. */
  Class<?> service();
}
