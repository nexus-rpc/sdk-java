package io.nexusrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Services are interfaces that contain {@link Operation} methods. For proxying reasons, this can
 * only be declared on an interface. This annotation is not inherited, so it must be on any
 * sub-interface with the same values or an error will occur. Similarly, an error will occur if an
 * implementation implements multiple interfaces with this annotation with any different values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {
  /** Override the name of the service. If not set, defaults to the unqualified interface name. */
  String name() default "";
}
