package io.nexusrpc.handler;

import io.nexusrpc.Service;
import java.lang.annotation.*;

/**
 * Marks a class as an implementation of a {@link Service} interface.
 *
 * <p>For every method in {@link #service}, this class must have an equivalent non-static, public
 * method annotated with {@link OperationImpl}. See that annotation's documentation for more
 * details.
 *
 * <p>This annotation is not inherited, it must be present on the instance used.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceImpl {
  /** The {@link Service}-annotated interface. */
  Class<?> service();
}
