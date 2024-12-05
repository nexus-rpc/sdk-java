package io.nexusrpc.handler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class ReflectionUtil {
  /** A map of Primitive types to their respective wrapper types. */
  private static final Map<Type, Type> primitiveWrapperTypeMap = new HashMap<>(9);

  static {
    primitiveWrapperTypeMap.put(void.class, Void.class);
    primitiveWrapperTypeMap.put(boolean.class, Boolean.class);
    primitiveWrapperTypeMap.put(byte.class, Byte.class);
    primitiveWrapperTypeMap.put(char.class, Character.class);
    primitiveWrapperTypeMap.put(double.class, Double.class);
    primitiveWrapperTypeMap.put(float.class, Float.class);
    primitiveWrapperTypeMap.put(int.class, Integer.class);
    primitiveWrapperTypeMap.put(long.class, Long.class);
    primitiveWrapperTypeMap.put(short.class, Short.class);
  }

  /**
   * Wrap a primitive boxed wrapper type for the given {@code type} if it is primitive.
   *
   * @param type a {@link Type} to return boxed wrapper type for
   * @return the boxed wrapper type for the give {@code type}, or {@code type} if no wrapper class
   *     was found.
   */
  static Type wrapTypeIfPrimitive(Type type) {
    return primitiveWrapperTypeMap.getOrDefault(type, type);
  }
}
