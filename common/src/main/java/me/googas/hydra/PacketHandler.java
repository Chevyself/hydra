package me.googas.hydra;

import lombok.NonNull;
import me.googas.hydra.io.PacketReader;

public interface PacketHandler {
  void handle(@NonNull ServiceMessenger messenger, @NonNull PacketReader packet);
}
