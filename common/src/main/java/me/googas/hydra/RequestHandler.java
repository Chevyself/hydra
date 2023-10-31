package me.googas.hydra;

import lombok.NonNull;
import me.googas.hydra.io.RequestReader;
import me.googas.hydra.io.ResponseWriter;

public interface RequestHandler {
  @NonNull
  void handle(
      @NonNull ServiceMessenger handler,
      @NonNull RequestReader request,
      @NonNull ResponseWriter response);
}
