package me.googas.hydra.server.impl;

import lombok.NonNull;
import me.googas.hydra.server.HydraServerClient;
import me.googas.hydra.server.HydraServerCommunicationHandler;

public class HydraServerCommunicationHandlerImpl implements HydraServerCommunicationHandler {

  @Override
  public void handle(@NonNull Throwable throwable) {
    throwable.printStackTrace();
  }

  @Override
  public void handle(@NonNull String message) {
    System.out.println(message);
  }

  @Override
  public void onConnect(@NonNull HydraServerClient client) {
    System.out.println("Client connected");
  }

  @Override
  public void onDisconnect(@NonNull HydraServerClient client) {
    System.out.println("Client disconnected");
  }
}
