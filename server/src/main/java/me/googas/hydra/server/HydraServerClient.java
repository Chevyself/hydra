package me.googas.hydra.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import lombok.Getter;
import lombok.NonNull;
import me.googas.hydra.HydraMessagingException;
import me.googas.hydra.ServiceMessenger;

public class HydraServerClient implements Runnable, Closeable {
  @Getter private final int internalId;
  @NonNull private final HydraServer server;
  @NonNull private final Socket socket;
  @Getter @NonNull private final ServiceMessenger messaging;
  private final Thread thread;
  private boolean closed;

  public HydraServerClient(int internalId, @NonNull HydraServer server, @NonNull Socket socket)
      throws IOException {
    this.internalId = internalId;
    this.server = server;
    this.socket = socket;
    this.messaging =
        new ServiceMessenger(
            server,
            socket.getInputStream(),
            socket.getOutputStream(),
            server.getCommunicationHandler());
    this.thread = new Thread(this);
    this.closed = false;
  }

  public void start() {
    this.thread.start();
  }

  @Override
  public void run() {
    while (!closed) {
      try {
        this.messaging.listen();
      } catch (HydraMessagingException e) {
        this.server.getCommunicationHandler().handle(e);
        this.closeQuietly();
      }
    }
  }

  private void closeQuietly() {
    try {
      this.close();
    } catch (IOException e) {
      this.server.getCommunicationHandler().handle(e);
    }
  }

  @Override
  public void close() throws IOException {
    this.closed = true;
    this.server.disconnect(this);
    this.socket.close();
  }
}
