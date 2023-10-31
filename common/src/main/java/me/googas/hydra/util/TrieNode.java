package me.googas.hydra.util;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import lombok.NonNull;
import me.googas.hydra.registry.Registry;

public interface TrieNode<V> extends Function<String, V>, Registry<String, V> {
  V getValue();

  @NonNull
  TrieNode<V> register(@NonNull String path, V value);

  TrieNode<V> getLiteralNode(String segment);

  @NonNull
  TrieNode<V> computeIfAbsent(@NonNull String key, @NonNull Supplier<TrieNode<V>> supplier);

  @NonNull
  static <V> TrieNode<V> getRoot(@NonNull String separator, @NonNull Pattern parameter) {
    TrieParser parser = new TrieParser(separator, parameter);
    return new TrieNodeImpl<>(parser, "", new ConcurrentHashMap<>(), null);
  }

  @NonNull
  static <V> TrieNode<V> getRoot(@NonNull String separator) {
    return getRoot(separator, Pattern.compile("\\{([^}]*)}"));
  }

  @NonNull
  static <V> TrieNode<V> getRoot() {
    return getRoot("/", Pattern.compile("\\{([^}]*)}"));
  }

  @Override
  default V apply(String s) {
    TrieNode<V> node = this.getNode(s);
    return node != null ? node.getValue() : null;
  }

  @Override
  default @NonNull Optional<V> get(@NonNull String string) {
    return Optional.ofNullable(this.apply(string));
  }

  TrieNode<V> getNode(@NonNull String path);

  TrieNode<V> getNodeRecursive(@NonNull String[] segments, int index);
}
