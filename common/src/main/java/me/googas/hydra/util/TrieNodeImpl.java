package me.googas.hydra.util;

import java.util.Map;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrieNodeImpl<V> implements TrieNode<V> {
  @NonNull private final TrieParser parser;
  @NonNull @Getter private final String segment;
  @NonNull private final Map<String, TrieNode<V>> map;
  @Getter private final V value;

  @Override
  public @NonNull TrieNode<V> register(@NonNull String path, V value) {
    parser.register(this, path, value);
    return this;
  }

  @Override
  public TrieNode<V> getNode(@NonNull String path) {
    return getNodeRecursive(path.split(parser.getSeparator()), 0);
  }

  @Override
  public TrieNode<V> getNodeRecursive(String[] segments, int index) {
    if (index >= segments.length) {
      return this;
    }
    String segment = segments[index];
    if (segment.isEmpty()) {
      return this.getNodeRecursive(segments, index + 1);
    }
    TrieNode<V> literal = getLiteralNode(segment);
    if (literal != null) {
      return literal.getNodeRecursive(segments, index + 1);
    } else {
      TrieNode<V> any = getLiteralNode("*");
      return any == null ? null : any.getNodeRecursive(segments, index + 1);
    }
  }

  @Override
  public TrieNode<V> getLiteralNode(String segment) {
    return map.get(segment);
  }

  @Override
  public TrieNode<V> computeIfAbsent(@NonNull String key, @NonNull Supplier<TrieNode<V>> supplier) {
    return map.computeIfAbsent(key, k -> supplier.get());
  }
}
