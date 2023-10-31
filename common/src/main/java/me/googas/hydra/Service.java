package me.googas.hydra;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.NonNull;

public interface Service extends Runnable {

  @NonNull
  Service start();

  @NonNull
  ThreadPoolExecutor getThreadPool();

  @NonNull
  ScheduledExecutorService getScheduler();

  @NonNull
  Messenger getMessaging();

  @NonNull
  Map<String, String> getMetadata();

  @NonNull
  Optional<PacketHandler> getPacketHandler(@NonNull String path);

  @NonNull
  Optional<RequestHandler> getRequestHandler(@NonNull String path);
}
