package me.googas.hydra.impl;

import lombok.NonNull;
import me.googas.hydra.ExceptionHandler;

public class ExceptionHandlerImpl implements ExceptionHandler {
  @Override
  public void handle(@NonNull Throwable throwable) {
    throwable.printStackTrace();
  }

  @Override
  public void handle(@NonNull String message) {
    System.out.println(message);
  }
}
