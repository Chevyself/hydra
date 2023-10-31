package me.googas.hydra.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.NonNull;
import me.googas.hydra.registry.Registry;

public class RegexMatchFunction<R> implements Function<String, R>, Registry<String, R> {

  @NonNull private static final Pattern pathCompilePattern = Pattern.compile("\\{([^}]*)}");
  @NonNull private final Map<Pattern, R> map = new HashMap<>();

  @NonNull
  public RegexMatchFunction<R> add(@NonNull String path, @NonNull R r) {
    this.map.put(Pattern.compile(pathCompilePattern.matcher(path).replaceAll("(?<$1>.*)")), r);
    return this;
  }

  @Override
  public R apply(String string) {
    if (string == null) return null;
    for (Map.Entry<Pattern, R> entry : map.entrySet()) {
      if (entry.getKey().matcher(string).matches()) {
        return entry.getValue();
      }
    }
    return null;
  }

  @Override
  public @NonNull Optional<R> get(@NonNull String string) {
    return Optional.ofNullable(this.apply(string));
  }
}
