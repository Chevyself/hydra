package me.googas.hydra.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TrieParser {
  @NonNull private final String separator;
  @NonNull private final Pattern parameter;

  public <V> void register(@NonNull TrieNode<V> trieNode, @NonNull String path, V value) {
    String[] split = path.split(separator);
    TrieNode<V> node = trieNode;
    for (String segment : split) {
      if (segment.isEmpty()) continue;
      boolean parameter = this.parameter.matcher(segment).matches();
      String key = parameter ? "*" : segment;
      node =
          node.computeIfAbsent(
              key, () -> new TrieNodeImpl<>(this, segment, new ConcurrentHashMap<>(), value));
    }
  }
}
