package me.googas.hydra.io;

import lombok.NonNull;
import me.googas.hydra.Request;
import me.googas.hydra.ServiceMessenger;

public class RequestReader extends MessageIO {
  @NonNull private final Request request;

  public RequestReader(ServiceMessenger messenger, @NonNull Request request) {
    super(messenger);
    this.request = request;
  }
}
