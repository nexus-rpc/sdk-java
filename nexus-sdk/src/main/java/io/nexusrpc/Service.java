package io.nexusrpc;

import java.lang.annotation.*;

/**
 * Services are interfaces that contain {@link Operation} methods.
 *
 * <p>This can only be declared on an interface. This annotation is not inherited, so it must be on
 * any sub-interface with the same name or an error will occur. Similarly, an error will occur if an
 * implementation implements multiple interfaces with this annotation with any different values.
 *
 * <p>All methods within (even on super interfaces) must be annotated with {@link Operation} and
 * must conform to the rules of that annotation. Two operations with the same name are not allowed.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {
  /** Override the name of the service. If not set, defaults to the unqualified interface name. */
  String name() default "";
}
