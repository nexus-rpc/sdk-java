package io.nexusrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Operations are interface methods defined on {@link Service} interfaces. This annotation is not
 * inherited, so it must be on any overridden signature in any sub-interface with the same values or
 * an error will occur. Similarly, an error will occur if an implementation has multiple operation
 * definitions of the same name with different signatures.
 *
 * <p>Return type is {@link java.util.concurrent.Future} for async operations, or any other value
 * for synchronous operations. The operation may accept no more than one parameter. All types must
 * be serializable.
 *
 * <p>Implementation details of an operation are not yet defined.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Operation {
  /** Override the name of the operation. If not set, defaults to the unqualified method name. */
  String name() default "";
}
