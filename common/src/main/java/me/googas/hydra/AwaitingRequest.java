package me.googas.hydra;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import lombok.Data;
import lombok.NonNull;

@Data
public class AwaitingRequest {
  @NonNull private final LocalDateTime sent;
  @NonNull private final CompletableFuture<Response> future;
}
