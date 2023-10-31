package me.googas.hydra.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import lombok.NonNull;
import me.googas.hydra.impl.AbstractHydraServiceBuilder;
import me.googas.hydra.server.impl.HydraServerCommunicationHandlerImpl;

public class HydraServerBuilder extends AbstractHydraServiceBuilder {
  private int maxClients = 10;

  @NonNull
  private HydraServerCommunicationHandler communicationHandler =
      new HydraServerCommunicationHandlerImpl();

  @Override
  public @NonNull HydraServerBuilder setPort(int port) {
    return (HydraServerBuilder) super.setPort(port);
  }

  @Override
  public @NonNull HydraServerBuilder setMinThreads(int minThreads) {
    return (HydraServerBuilder) super.setMinThreads(minThreads);
  }

  @Override
  public @NonNull HydraServerBuilder setMaxThreads(int maxThreads) {
    return (HydraServerBuilder) super.setMaxThreads(maxThreads);
  }

  @Override
  public @NonNull HydraServerBuilder setScheduledTasks(int scheduledTasks) {
    return (HydraServerBuilder) super.setScheduledTasks(scheduledTasks);
  }

  @Override
  public @NonNull HydraServerBuilder setKeepAlive(Duration keepAlive) {
    return (HydraServerBuilder) super.setKeepAlive(keepAlive);
  }

  @Override
  public @NonNull HydraServerBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
    return (HydraServerBuilder) super.setWorkQueue(workQueue);
  }

  public @NonNull HydraServerBuilder setMaxClients(int maxClients) {
    this.maxClients = maxClients;
    return this;
  }

  public @NonNull HydraServerBuilder setCommunicationHandler(
      HydraServerCommunicationHandler communicationHandler) {
    this.communicationHandler = communicationHandler;
    return this;
  }

  @NonNull
  public HydraServer build() throws HydraServerException {
    try {
      return new HydraServer(
          new ServerSocket(this.port),
          createThreadPool(),
          createScheduler(),
          this.communicationHandler,
          this.maxClients,
          this.packetRegistry,
          this.requestRegistry);
    } catch (IOException e) {
      throw new HydraServerException("Could not create server socket", e);
    }
  }
}
