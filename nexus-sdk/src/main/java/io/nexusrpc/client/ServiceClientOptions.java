package io.nexusrpc.client;

import io.nexusrpc.Experimental;
import io.nexusrpc.Serializer;
import io.nexusrpc.ServiceDefinition;
import io.nexusrpc.client.transport.Transport;
import java.util.Objects;

/** Options to configure a Nexus service client. */
@Experimental
public class ServiceClientOptions<T> {
  /** Create a builder for ServiceClientOptions. */
  public static <T> Builder<T> newBuilder(Class<T> serviceClass) {
    return new Builder<>(serviceClass);
  }

  private final Class<T> serviceClass;
  private final ServiceDefinition serviceDefinition;
  private final Transport transport;
  private final Serializer serializer;

  private ServiceClientOptions(
      Class<T> serviceClass,
      ServiceDefinition serviceDefinition,
      Transport transport,
      Serializer serializer) {
    this.serviceClass = serviceClass;
    this.serviceDefinition = serviceDefinition;
    this.transport = transport;
    this.serializer = serializer;
  }

  public Class<T> getServiceClass() {
    return serviceClass;
  }

  public ServiceDefinition getServiceDefinition() {
    return serviceDefinition;
  }

  public Transport getTransport() {
    return transport;
  }

  public Serializer getSerializer() {
    return serializer;
  }

  public static class Builder<R> {
    private final Class<R> serviceClass;
    private final ServiceDefinition serviceDefinition;
    private Transport transport;
    private Serializer serializer;

    private Builder(Class<R> serviceClass) {
      this.serviceClass = serviceClass;
      // Create the service definition here to validate the service class.
      this.serviceDefinition = ServiceDefinition.fromClass(serviceClass);
    }

    public Builder<R> setTransport(Transport transport) {
      this.transport = transport;
      return this;
    }

    public Builder<R> setSerializer(Serializer serializer) {
      this.serializer = serializer;
      return this;
    }

    public ServiceClientOptions<R> build() {
      Objects.requireNonNull(transport, "Transport must be set");
      Objects.requireNonNull(serializer, "Serializer must be set");
      return new ServiceClientOptions<>(serviceClass, serviceDefinition, transport, serializer);
    }
  }
}
