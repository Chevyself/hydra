package me.googas.hydra;

import lombok.NonNull;

public interface ExceptionHandler {
  void handle(@NonNull Throwable throwable);

  void handle(@NonNull String message);
}
