package io.nexusrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Operations are interface methods defined on {@link Service} interfaces.
 *
 * <p>This annotation is not inherited, so it must be on any overridden signature in any
 * sub-interface with the same values or an error will occur. Similarly, an error will occur if an
 * implementation has multiple operation definitions of the same name with different signatures.
 *
 * <p>An operation can only define zero or one parameter that supports conversion. The return type
 * can be <c>void</c> or a type that supports conversion. An operation declaration cannot have a
 * <c>throws</c> clause. An operation cannot have a default implementation and cannot be static on
 * the interface.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Operation {
  /** Override the name of the operation. If not set, defaults to the unqualified method name. */
  String name() default "";
}
