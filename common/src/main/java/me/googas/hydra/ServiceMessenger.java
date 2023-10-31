package me.googas.hydra;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.hydra.io.PacketReader;
import me.googas.hydra.io.RequestReader;
import me.googas.hydra.io.ResponseWriter;

public class ServiceMessenger implements Messenger {
  @NonNull private final Service service;
  @NonNull private final InputStream input;
  @NonNull private final OutputStream output;
  @NonNull private final ExceptionHandler exceptionHandler;
  @NonNull private final Map<Integer, AwaitingRequest> requests;

  public ServiceMessenger(
      @NonNull Service service,
      @NonNull InputStream input,
      @NonNull OutputStream output,
      @NonNull ExceptionHandler exceptionHandler) {
    this.service = service;
    this.input = input;
    this.output = output;
    this.exceptionHandler = exceptionHandler;
    this.requests = new ConcurrentHashMap<>();
  }

  @NonNull
  public CompletableFuture<Response> send(@NonNull Request.Builder requestBuilder) {
    CompletableFuture<Response> future = new CompletableFuture<>();
    requestBuilder
        .setTimestamp(LocalDateTime.now().toString())
        .setId(this.nextRequestId())
        .putAllMetadata(service.getMetadata());
    this.send(Message.newBuilder().setType(MessageType.REQUEST).setRequest(requestBuilder));
    this.requests.put(requestBuilder.getId(), new AwaitingRequest(LocalDateTime.now(), future));
    return future;
  }

  public void send(@NonNull Response response) {
    this.send(Message.newBuilder().setType(MessageType.RESPONSE).setResponse(response));
  }

  @Override
  public void send(
      @NonNull Request.Builder requestBuilder,
      @NonNull Consumer<Response> responseConsumer,
      @NonNull Consumer<Throwable> throwableConsumer) {
    this.send(requestBuilder)
        .whenComplete(
            (response, throwable) -> {
              if (throwable != null) {
                throwableConsumer.accept(throwable);
                return;
              }
              responseConsumer.accept(response);
            });
  }

  @Override
  public void send(Request.Builder requestBuilder, @NonNull Consumer<Response> responseConsumer) {
    this.send(requestBuilder, responseConsumer, this.exceptionHandler::handle);
  }

  @Override
  public void send(@NonNull Packet packet) {
    this.send(Message.newBuilder().setType(MessageType.PACKET).setPacket(packet));
  }

  public void send(@NonNull Message.Builder builder) {
    this.send(builder.build());
  }

  public void send(@NonNull Message message) {
    try {
      message.writeDelimitedTo(this.output);
    } catch (IOException e) {
      this.exceptionHandler.handle(e);
    }
  }

  @Override
  public void listen() throws HydraMessagingException {
    try {
      Message message;
      while ((message = Message.parseDelimitedFrom(this.input)) != null) {
        switch (message.getType()) {
          case PACKET:
            this.handlePacket(message.getPacket());
            break;
          case REQUEST:
            this.handleRequest(message.getRequest());
            break;
          case RESPONSE:
            this.handleResponse(message.getResponse());
            break;
          default:
            throw new HydraMessagingException("Unknown message type " + message.getType());
        }
      }
    } catch (IOException e) {
      throw new HydraMessagingException("Failed to read message", e);
    }
  }

  private void handlePacket(@NonNull Packet packet) {
    Optional<PacketHandler> optional = this.service.getPacketHandler(packet.getPath());
    if (optional.isPresent()) {
      optional.get().handle(this, new PacketReader(this, packet));
    } else {
      this.exceptionHandler.handle(
          new HydraMessagingException("No packet handler for packet in path: " + packet.getPath()));
    }
  }

  private void handleRequest(@NonNull Request request) {
    this.service
        .getThreadPool()
        .execute(
            () -> {
              Optional<RequestHandler> optional = this.service.getRequestHandler(request.getPath());
              if (optional.isPresent()) {
                try {
                  optional
                      .get()
                      .handle(this, new RequestReader(request, request), new ResponseWriter(this, request));
                } catch (Exception e) {
                  this.send(this.newError(request, 500, "Internal error: " + e.getMessage()));
                  throw new HydraFatalException("Failed to handle request", e);
                }
              } else {
                this.send(
                    this.newError(
                        request, 404, "No request handler for path: " + request.getPath()));
                this.exceptionHandler.handle(
                    "Failed to find request handler for path: " + request.getPath());
              }
            });
  }

  private Response newError(@NonNull Request request, int code, @NonNull String message) {
    return Response.newBuilder()
        .setId(request.getId())
        .setTimestamp(LocalDateTime.now().toString())
        .setCode(code)
        .setError(message)
        .build();
  }

  private void handleResponse(@NonNull Response response) {
    AwaitingRequest awaitingRequest = this.requests.remove(response.getId());
    if (awaitingRequest != null) {
      awaitingRequest.getFuture().complete(response);
    } else {
      this.exceptionHandler.handle(
          new HydraMessagingException(
              "No awaiting request for response with id: " + response.getId()));
    }
  }

  private int nextRequestId() {
    int id = this.requests.size() + 1;
    do {
      id++;
    } while (this.requests.containsKey(id));
    return id;
  }
}
