package me.googas.hydra.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Getter;
import lombok.NonNull;
import me.googas.hydra.Messenger;
import me.googas.hydra.PacketHandler;
import me.googas.hydra.RequestHandler;
import me.googas.hydra.Service;
import me.googas.hydra.registry.Registry;

public class HydraServer implements Service, Closeable {

  @NonNull private final ServerSocket server;
  @Getter @NonNull private final ThreadPoolExecutor threadPool;
  @Getter @NonNull private final ScheduledExecutorService scheduler;
  @Getter @NonNull private final Messenger messaging;
  @NonNull private final Registry<String, PacketHandler> packetRegistry;
  @NonNull private final Registry<String, RequestHandler> requestRegistry;
  @NonNull @Getter private final HydraServerCommunicationHandler communicationHandler;
  private final int maxClients;
  private final Map<Integer, HydraServerClient> clients;
  private boolean running;
  private Thread thread;

  HydraServer(
      @NonNull ServerSocket server,
      @NonNull ThreadPoolExecutor threadPool,
      @NonNull ScheduledExecutorService scheduler,
      @NonNull HydraServerCommunicationHandler communicationHandler,
      int maxClients,
      @NonNull Registry<String, PacketHandler> packetRegistry,
      @NonNull Registry<String, RequestHandler> requestRegistry) {
    this.server = server;
    this.threadPool = threadPool;
    this.scheduler = scheduler;
    this.messaging = new ForwardingMessenger(this);
    this.communicationHandler = communicationHandler;
    this.maxClients = maxClients;
    this.packetRegistry = packetRegistry;
    this.requestRegistry = requestRegistry;
    this.clients = new ConcurrentHashMap<>();
    this.running = false;
    this.thread = new Thread(this);
  }

  @Override
  public HydraServer start() {
    thread.start();
    this.running = true;
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
    while (running) {
      try {
        Socket newClient = this.server.accept();
        if (this.clients.size() >= this.maxClients) {
          this.reject(newClient);
          continue;
        }
        int id = this.getNextInternalId();
        HydraServerClient client = new HydraServerClient(id, this, newClient);
        client.start();
        this.clients.put(id, client);
        this.communicationHandler.onConnect(client);
      } catch (Throwable throwable) {
        this.communicationHandler.handle(throwable);
      }
    }
  }

  private void reject(Socket newClient) {
    try (PrintWriter writer = new PrintWriter(newClient.getOutputStream())) {
      writer.println("Connection rejected, max connections reached");
      writer.flush();
      newClient.close();
    } catch (IOException e) {
      this.communicationHandler.handle(e);
    }
  }

  private int getNextInternalId() {
    int id = clients.size();
    while (clients.containsKey(id)) {
      id++;
    }
    return id;
  }

  @Override
  public void close() throws IOException {
    this.running = false;
    this.thread = null;
    this.server.close();
    this.clients.clear();
  }

  public void disconnect(@NonNull HydraServerClient client) {
    this.clients.remove(client.getInternalId());
    this.communicationHandler.onDisconnect(client);
  }

  @NonNull
  Set<HydraServerClient> getClients() {
    return new HashSet<>(this.clients.values());
  }
}
