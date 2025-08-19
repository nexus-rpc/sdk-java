package io.nexusrpc.handler;

import io.nexusrpc.Link;
import io.nexusrpc.ServiceDefinition;
import java.time.Instant;
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
  private final Instant deadline;
  private final List<Link> links;
  private final @Nullable ServiceDefinition serviceDefinition;

  private OperationContext(
      String service,
      String operation,
      Map<String, String> headers,
      @Nullable OperationMethodCanceller methodCanceller,
      Instant deadline,
      @Nullable ServiceDefinition serviceDefinition,
      List<Link> links) {
    this.service = service;
    this.operation = operation;
    this.headers = headers;
    this.methodCanceller = methodCanceller;
    this.deadline = deadline;
    this.serviceDefinition = serviceDefinition;
    this.links = links;
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

  /** Get a read only list of links attached to this context. */
  public List<Link> getLinks() {
    return Collections.unmodifiableList(new ArrayList<>(links));
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

  /** Get the service definition associated with this operation context, if any. */
  public @Nullable ServiceDefinition getServiceDefinition() {
    return serviceDefinition;
  }

  /**
   * Get the deadline for the operation handler method. This is the time by which the method should
   * complete. This is not the operation's deadline.
   */
  public @Nullable Instant getDeadline() {
    return deadline;
  }

  /**
   * Add a listener for method cancellation. This will be invoked immediately before this function
   * returns if the method is already cancelled. The listener must not block. This is not reentrant
   * and therefore must not be called in another cancellation listener.
   *
   * @return this
   */
  public OperationContext addMethodCancellationListener(
      OperationMethodCancellationListener listener) {
    if (methodCanceller != null) {
      methodCanceller.addListener(listener);
    }
    return this;
  }

  /**
   * Associates links with the current operation to be propagated back to the caller. Links will
   * only be attached on successful responses.
   *
   * @return this
   */
  public OperationContext addLinks(Link... links) {
    this.links.addAll(Arrays.asList(links));
    return this;
  }

  /**
   * Associates links with the current operation to be propagated back to the caller. It is
   * recommended to use {@link #addLinks(Link...)} to avoid accidental override. Links will only be
   * attached on successful responses.
   *
   * @return this
   */
  public OperationContext setLinks(Link... links) {
    this.links.clear();
    this.links.addAll(Arrays.asList(links));
    return this;
  }

  /**
   * Remove a listener, if present, for method cancellation using hash code. This is not reentrant
   * and therefore must not be called in another cancellation listener.
   */
  public OperationContext removeMethodCancellationListener(
      OperationMethodCancellationListener listener) {
    if (methodCanceller != null) {
      methodCanceller.removeListener(listener);
    }
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    OperationContext that = (OperationContext) o;
    return Objects.equals(service, that.service)
        && Objects.equals(operation, that.operation)
        && Objects.equals(headers, that.headers)
        && Objects.equals(methodCanceller, that.methodCanceller)
        && Objects.equals(deadline, that.deadline)
        && Objects.equals(links, that.links)
        && Objects.equals(serviceDefinition, that.serviceDefinition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        service, operation, headers, methodCanceller, deadline, links, serviceDefinition);
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
        + ", methodCanceller="
        + methodCanceller
        + ", deadline="
        + deadline
        + ", links="
        + links
        + ", serviceDefinition="
        + serviceDefinition
        + '}';
  }

  /** Builder for operation context. */
  public static class Builder {
    private @Nullable String service;
    private @Nullable String operation;
    private final SortedMap<String, String> headers;
    private @Nullable OperationMethodCanceller methodCanceller;
    private @Nullable Instant deadline;
    private @Nullable ServiceDefinition serviceDefinition;
    // Currently links are not set in the builder, but they need to be passed though to go from
    // OperationContext -> Builder
    // and back to OperationContext, so we keep them here.
    private final List<Link> links;

    private Builder() {
      headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      links = new ArrayList<>();
    }

    private Builder(OperationContext context) {
      service = context.service;
      operation = context.operation;
      headers = new TreeMap<>(context.headers);
      methodCanceller = context.methodCanceller;
      deadline = context.deadline;
      serviceDefinition = context.serviceDefinition;
      links = context.links;
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

    /** Set the deadline for the operation handler method. */
    public Builder setDeadline(Instant deadline) {
      this.deadline = deadline;
      return this;
    }

    /** Sets the service definition. */
    public Builder setServiceDefinition(ServiceDefinition serviceDefinition) {
      this.serviceDefinition = serviceDefinition;
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
          methodCanceller,
          deadline,
          serviceDefinition,
          links);
    }
  }
}
