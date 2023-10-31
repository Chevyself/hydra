package me.googas.hydra.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.hydra.HydraMessagingException;
import me.googas.hydra.Messenger;
import me.googas.hydra.Packet;
import me.googas.hydra.Request;
import me.googas.hydra.Response;

public final class ForwardingMessenger implements Messenger {
  @NonNull private final HydraServer server;

  public ForwardingMessenger(@NonNull HydraServer server) {
    this.server = server;
  }

  public void send(
      @NonNull Request.Builder requestBuilder,
      @NonNull BiConsumer<HydraServerClient, Response> responseConsumer,
      @NonNull BiConsumer<HydraServerClient, Throwable> throwableConsumer) {
    this.server
        .getClients()
        .forEach(
            client -> {
              client
                  .getMessaging()
                  .send(requestBuilder)
                  .whenComplete(
                      (response, e) -> {
                        if (e != null) {
                          throwableConsumer.accept(client, e);
                          return;
                        }
                        responseConsumer.accept(client, response);
                      });
            });
  }

  public void send(
      @NonNull Request.Builder requestBuilder,
      @NonNull BiConsumer<HydraServerClient, Response> responseConsumer) {
    this.send(
        requestBuilder,
        responseConsumer,
        (client, throwable) -> {
          this.server
              .getCommunicationHandler()
              .handle(
                  new HydraServerException(
                      "Failed to send request to client " + client.getInternalId(), throwable));
        });
  }

  @Override
  public void send(
      @NonNull Request.Builder requestBuilder,
      @NonNull Consumer<Response> responseConsumer,
      @NonNull Consumer<Throwable> throwableConsumer) {
    this.send(
        requestBuilder,
        ((client, response) -> {
          responseConsumer.accept(response);
        }),
        ((client, throwable) -> {
          throwableConsumer.accept(throwable);
        }));
  }

  @Override
  public void send(
      @NonNull Request.Builder requestBuilder, @NonNull Consumer<Response> responseConsumer) {
    this.send(
        requestBuilder,
        ((client, response) -> {
          responseConsumer.accept(response);
        }));
  }

  @Override
  public void send(@NonNull Packet packet) {
    this.server.getClients().forEach(client -> client.getMessaging().send(packet));
  }

  @Override
  public void listen() throws HydraMessagingException {
    // Empty
  }
}
