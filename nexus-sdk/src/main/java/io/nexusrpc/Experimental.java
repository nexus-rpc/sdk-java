package io.nexusrpc;

import java.lang.annotation.*;

/**
 * Annotation that specifies that an element is experimental, has unstable API or may change without
 * notice. This annotation is inherited.
 */
@Inherited
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface Experimental {}
