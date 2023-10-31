package me.googas.hydra.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Getter;
import lombok.NonNull;
import me.googas.hydra.ExceptionHandler;
import me.googas.hydra.HydraMessagingException;
import me.googas.hydra.Messenger;
import me.googas.hydra.PacketHandler;
import me.googas.hydra.RequestHandler;
import me.googas.hydra.Service;
import me.googas.hydra.ServiceMessenger;
import me.googas.hydra.registry.Registry;

public class HydraClient implements Service, Closeable {

  @NonNull private final Socket socket;
  @NonNull private final ExceptionHandler exceptionHandler;
  @Getter @NonNull private final ThreadPoolExecutor threadPool;
  @Getter @NonNull private final ScheduledExecutorService scheduler;
  @Getter @NonNull private final Messenger messaging;
  @NonNull private final Registry<String, PacketHandler> packetRegistry;
  @NonNull private final Registry<String, RequestHandler> requestRegistry;
  @NonNull private final Thread thread;
  private boolean running;

  HydraClient(
      @NonNull Socket socket,
      @NonNull ExceptionHandler exceptionHandler,
      @NonNull ThreadPoolExecutor threadPool,
      @NonNull ScheduledExecutorService scheduler,
      @NonNull Registry<String, PacketHandler> packetRegistry,
      @NonNull Registry<String, RequestHandler> requestRegistry)
      throws IOException {
    this.socket = socket;
    this.exceptionHandler = exceptionHandler;
    this.threadPool = threadPool;
    this.scheduler = scheduler;
    this.packetRegistry = packetRegistry;
    this.requestRegistry = requestRegistry;
    this.messaging =
        new ServiceMessenger(
            this, socket.getInputStream(), socket.getOutputStream(), exceptionHandler);
    this.thread = new Thread(this);
    this.running = true;
  }

  @Override
  public void close() throws IOException {
    this.running = false;
    this.socket.close();
  }

  @Override
  public @NonNull HydraClient start() {
    this.thread.start();
    return this;
  }

  @Override
  public @NonNull Map<String, String> getMetadata() {
    return new HashMap<>();
  }

  @Override
  public @NonNull Optional<PacketHandler> getPacketHandler(@NonNull String path) {
    return this.packetRegistry.get(path);
  }

  @Override
  public @NonNull Optional<RequestHandler> getRequestHandler(@NonNull String path) {
    return this.requestRegistry.get(path);
  }

  @Override
  public void run() {
    while (this.running) {
      try {
        messaging.listen();
      } catch (HydraMessagingException e) {
        exceptionHandler.handle(e);
      }
    }
  }
}
