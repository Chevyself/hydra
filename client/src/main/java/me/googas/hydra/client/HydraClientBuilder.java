package me.googas.hydra.client;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import lombok.NonNull;
import me.googas.hydra.ExceptionHandler;
import me.googas.hydra.PacketHandler;
import me.googas.hydra.RequestHandler;
import me.googas.hydra.impl.AbstractHydraServiceBuilder;
import me.googas.hydra.impl.ExceptionHandlerImpl;
import me.googas.hydra.registry.Registry;

public class HydraClientBuilder extends AbstractHydraServiceBuilder {
  @NonNull private String host = "localhost";
  @NonNull private ExceptionHandler exceptionHandler = new ExceptionHandlerImpl();

  @NonNull
  public HydraClientBuilder setHost(String host) {
    this.host = host;
    return this;
  }

  @NonNull
  public HydraClientBuilder setExceptionHandler(ExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
    return this;
  }

  @Override
  public @NonNull HydraClientBuilder setPort(int port) {
    return (HydraClientBuilder) super.setPort(port);
  }

  @Override
  public @NonNull HydraClientBuilder setMinThreads(int minThreads) {
    return (HydraClientBuilder) super.setMinThreads(minThreads);
  }

  @Override
  public @NonNull HydraClientBuilder setMaxThreads(int maxThreads) {
    return (HydraClientBuilder) super.setMaxThreads(maxThreads);
  }

  @Override
  public @NonNull HydraClientBuilder setScheduledTasks(int scheduledTasks) {
    return (HydraClientBuilder) super.setScheduledTasks(scheduledTasks);
  }

  @Override
  public @NonNull HydraClientBuilder setKeepAlive(Duration keepAlive) {
    return (HydraClientBuilder) super.setKeepAlive(keepAlive);
  }

  @Override
  public @NonNull HydraClientBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
    return (HydraClientBuilder) super.setWorkQueue(workQueue);
  }

  @Override
  public @NonNull HydraClientBuilder setPacketRegistry(
      Registry<String, PacketHandler> packetRegistry) {
    return (HydraClientBuilder) super.setPacketRegistry(packetRegistry);
  }

  @Override
  public @NonNull HydraClientBuilder setRequestRegistry(
      Registry<String, RequestHandler> requestRegistry) {
    return (HydraClientBuilder) super.setRequestRegistry(requestRegistry);
  }

  public HydraClient build() throws HydraClientException {
    try {
      return new HydraClient(
          new Socket(this.host, this.port),
          exceptionHandler,
          this.createThreadPool(),
          this.createScheduler(),
          this.packetRegistry,
          this.requestRegistry);
    } catch (IOException e) {
      throw new HydraClientException("Failed to connect to " + this.host + ":" + this.port, e);
    }
  }
}
