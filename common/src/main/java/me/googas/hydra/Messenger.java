package me.googas.hydra;

import java.util.function.Consumer;
import lombok.NonNull;

public interface Messenger {

  void send(
      @NonNull Request.Builder requestBuilder,
      @NonNull Consumer<Response> responseConsumer,
      @NonNull Consumer<Throwable> throwableConsumer);

  void send(@NonNull Request.Builder requestBuilder, @NonNull Consumer<Response> responseConsumer);

  void send(@NonNull Packet packet);

  void listen() throws HydraMessagingException;
}
