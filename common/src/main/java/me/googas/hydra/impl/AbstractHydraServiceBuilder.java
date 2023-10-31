package me.googas.hydra.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.NonNull;
import me.googas.hydra.PacketHandler;
import me.googas.hydra.RequestHandler;
import me.googas.hydra.registry.CachedRegistry;
import me.googas.hydra.registry.Registry;

public abstract class AbstractHydraServiceBuilder {
  protected int port = 3000;
  protected int minThreads = 5;
  protected int maxThreads = 10;
  protected int scheduledTasks = 3;
  @NonNull protected Duration keepAlive = Duration.ofMinutes(1);
  @NonNull protected BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
  @NonNull protected Registry<String, PacketHandler> packetRegistry = new CachedRegistry<>();
  @NonNull protected Registry<String, RequestHandler> requestRegistry = new CachedRegistry<>();

  @NonNull
  public AbstractHydraServiceBuilder setPort(int port) {
    this.port = port;
    return this;
  }

  @NonNull
  public AbstractHydraServiceBuilder setMinThreads(int minThreads) {
    this.minThreads = minThreads;
    return this;
  }

  @NonNull
  public AbstractHydraServiceBuilder setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads;
    return this;
  }

  @NonNull
  public AbstractHydraServiceBuilder setScheduledTasks(int scheduledTasks) {
    this.scheduledTasks = scheduledTasks;
    return this;
  }

  @NonNull
  public AbstractHydraServiceBuilder setKeepAlive(Duration keepAlive) {
    this.keepAlive = keepAlive;
    return this;
  }

  @NonNull
  public AbstractHydraServiceBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
    this.workQueue = workQueue;
    return this;
  }

  @NonNull
  public AbstractHydraServiceBuilder setPacketRegistry(
      Registry<String, PacketHandler> packetRegistry) {
    this.packetRegistry = packetRegistry;
    return this;
  }

  @NonNull
  public AbstractHydraServiceBuilder setRequestRegistry(
      Registry<String, RequestHandler> requestRegistry) {
    this.requestRegistry = requestRegistry;
    return this;
  }

  @NonNull
  protected ScheduledThreadPoolExecutor createScheduler() {
    return new ScheduledThreadPoolExecutor(scheduledTasks);
  }

  @NonNull
  protected ThreadPoolExecutor createThreadPool() {
    return new ThreadPoolExecutor(
        this.minThreads,
        this.maxThreads,
        this.keepAlive.toMillis(),
        TimeUnit.MILLISECONDS,
        this.workQueue);
  }
}
