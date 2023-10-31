package me.googas.hydra.io;

import lombok.NonNull;
import me.googas.hydra.Packet;
import me.googas.hydra.ServiceMessenger;


public class PacketReader extends MessageIO {
  @NonNull private final Packet packet;

  public PacketReader(@NonNull ServiceMessenger messenger, @NonNull Packet packet) {
    super(messenger);
    this.packet = packet;
  }
}
