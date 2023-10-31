package me.googas.hydra.server;

import lombok.NonNull;
import me.googas.hydra.ExceptionHandler;

public interface HydraServerCommunicationHandler extends ExceptionHandler {
  void onConnect(@NonNull HydraServerClient client);

  void onDisconnect(@NonNull HydraServerClient client);
}
