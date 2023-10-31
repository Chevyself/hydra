package me.googas.hydra.registry;

import java.util.Optional;
import lombok.NonNull;

public interface Registry<T, R> {

  @NonNull
  Optional<R> get(@NonNull T t);
}
