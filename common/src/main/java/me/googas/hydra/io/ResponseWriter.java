package me.googas.hydra.io;

import java.time.LocalDateTime;
import lombok.NonNull;
import me.googas.hydra.Request;
import me.googas.hydra.Response;
import me.googas.hydra.ServiceMessenger;

public class ResponseWriter extends MessageIO {

  @NonNull private final Response.Builder builder;
  private boolean sent;

  public ResponseWriter(@NonNull ServiceMessenger messenger, @NonNull Request request) {
    super(messenger);
    this.builder =
        Response.newBuilder().setId(request.getId()).setTimestamp(request.getTimestamp());
    this.sent = false;
  }

  private void checkSent() {
    if (this.sent) {
      throw new IllegalStateException("Response already sent");
    }
    this.sent = true;
  }

  public void error(int code, String error) {
    this.checkSent();
    this.messenger.send(
        this.builder
            .setTimestamp(LocalDateTime.now().toString())
            .setCode(code)
            .setError(error)
            .build());
  }

  public void error(int code) {
    this.error(code, "");
  }

  public void error(String error) {
    this.error(500, error);
  }

  public void error() {
    this.error(500, "");
  }

  public void send(int code, String body) {
    this.checkSent();
    this.messenger.send(
        this.builder
            .setTimestamp(LocalDateTime.now().toString())
            .setCode(code)
            .setBody(body)
            .build());
  }

  public void send(int code) {
    this.send(code, "");
  }

  public void send(String body) {
    this.send(200, body);
  }

  public void send() {
    this.send(200, "");
  }
}
