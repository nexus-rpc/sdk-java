package io.nexusrpc.handler;

import java.util.*;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

/** Context for use in operation handling. */
public class OperationContext {
  /** Create a builder. */
  public static Builder newBuilder() {
    return new Builder();
  }

  /** Create a builder from an existing context. */
  public static Builder newBuilder(OperationContext context) {
    return new Builder(context);
  }

  private final String service;
  private final String operation;
  private final Map<String, String> headers;
  // This is not included in equals, hashCode, or toString
  private final @Nullable OperationMethodCanceller methodCanceller;

  private OperationContext(
      String service,
      String operation,
      Map<String, String> headers,
      @Nullable OperationMethodCanceller methodCanceller) {
    this.service = service;
    this.operation = operation;
    this.headers = headers;
    this.methodCanceller = methodCanceller;
  }

  /** Service name for the call. */
  public String getService() {
    return service;
  }

  /** Operation name for the call. */
  public String getOperation() {
    return operation;
  }

  /** Headers for the call. The returned map operates without regard to case. */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /**
   * True if the method has been cancelled, false if not. Note, this is method cancellation,
   * unrelated to operation cancellation.
   */
  public boolean isMethodCancelled() {
    return getMethodCancellationReason() != null;
  }

  /**
   * Reason the method was cancelled or null if not canceled. Note, this is method cancellation,
   * unrelated to operation cancellation.
   */
  public @Nullable String getMethodCancellationReason() {
    return methodCanceller == null ? null : methodCanceller.getCancellationReason();
  }

  /**
   * Add a listener for method cancellation. This will be invoked immediately before this function
   * returns if the method is already cancelled. The listener must not block. This is not reentrant
   * and therefore must not be called in another cancellation listener.
   */
  public void addMethodCancellationListener(OperationMethodCancellationListener listener) {
    if (methodCanceller != null) {
      methodCanceller.addListener(listener);
    }
  }

  /**
   * Remove a listener, if present, for method cancellation using hash code. This is not reentrant
   * and therefore must not be called in another cancellation listener.
   */
  public void removeMethodCancellationListener(OperationMethodCancellationListener listener) {
    if (methodCanceller != null) {
      methodCanceller.removeListener(listener);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationContext that = (OperationContext) o;
    return Objects.equals(service, that.service)
        && Objects.equals(operation, that.operation)
        && Objects.equals(headers, that.headers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(service, operation, headers);
  }

  @Override
  public String toString() {
    return "OperationContext{"
        + "service='"
        + service
        + '\''
        + ", operation='"
        + operation
        + '\''
        + ", headers="
        + headers
        + '}';
  }

  /** Builder for operation context. */
  public static class Builder {
    private @Nullable String service;
    private @Nullable String operation;
    private final SortedMap<String, String> headers;
    private @Nullable OperationMethodCanceller methodCanceller;

    private Builder() {
      headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    private Builder(OperationContext context) {
      service = context.service;
      operation = context.operation;
      headers = new TreeMap<>(context.headers);
    }

    /** Set service. Required. */
    public Builder setService(String service) {
      this.service = service;
      return this;
    }

    /** Set operation name. Required. */
    public Builder setOperation(String operation) {
      this.operation = operation;
      return this;
    }

    /** Get headers to mutate. The returned map operates without regard to case. */
    public Map<String, String> getHeaders() {
      return headers;
    }

    /** Put a header into the header map. */
    public Builder putHeader(String key, String value) {
      headers.put(key, value);
      return this;
    }

    /** Set method canceller. */
    public Builder setMethodCanceller(OperationMethodCanceller methodCanceller) {
      this.methodCanceller = methodCanceller;
      return this;
    }

    /** Build the context. */
    public OperationContext build() {
      Objects.requireNonNull(service, "Service required");
      Objects.requireNonNull(operation, "Operation required");
      SortedMap<String, String> normalizedHeaders =
          headers.entrySet().stream()
              .collect(
                  Collectors.toMap(
                      (k) -> k.getKey().toLowerCase(),
                      Map.Entry::getValue,
                      (a, b) -> a,
                      () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));
      return new OperationContext(
          service,
          operation,
          Collections.unmodifiableMap(new TreeMap<>(normalizedHeaders)),
          methodCanceller);
    }
  }
}
