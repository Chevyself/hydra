package me.googas.hydra.registry;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;

public class CachedRegistry<R> implements Registry<String, R> {

  @NonNull private final List<Function<String, R>> providers = new ArrayList<>();
  @NonNull private final Map<String, WeakReference<R>> cache = new HashMap<>();

  @NonNull
  public CachedRegistry<R> addProvider(@NonNull Function<String, R> handlerProvider) {
    this.providers.add(handlerProvider);
    return this;
  }

  @Override
  public @NonNull Optional<R> get(@NonNull String string) {
    WeakReference<R> cached = cache.get(string);
    if (cached != null && cached.get() != null) {
      return Optional.ofNullable(cached.get());
    }
    for (Function<String, R> handler : this.providers) {
      R applied = handler.apply(string);
      if (applied != null) {
        // this.cache.put(string, new WeakReference<>(applied));
        return Optional.of(applied);
      }
    }
    return Optional.empty();
  }
}
