package io.nexusrpc;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.Nullable;

public class StringOnlySerializer implements Serializer {
  @Override
  public Content serialize(@Nullable Object value) {
    String str;
    if (value == null) {
      str = "";
    } else if (value instanceof String) {
      str = (String) value;
    } else {
      throw new IllegalArgumentException("Only string types accepted");
    }
    return Content.newBuilder().setData(str.getBytes(StandardCharsets.UTF_8)).build();
  }

  @Override
  public @Nullable Object deserialize(Content content, Type type) {
    if (!type.equals(String.class)) {
      throw new IllegalArgumentException("Only string types accepted");
    }
    return new String(content.getData(), StandardCharsets.UTF_8);
  }
}
