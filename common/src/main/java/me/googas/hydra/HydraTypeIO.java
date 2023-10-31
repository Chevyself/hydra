package me.googas.hydra;

import lombok.NonNull;
import me.googas.hydra.util.TypeReference;

public interface HydraTypeIO {
    <T> T read(@NonNull String string, @NonNull Class<T> clazz);

    <T> T readGeneric(@NonNull String string, @NonNull TypeReference<T> typeReference);

    @NonNull
    String write(@NonNull Object object);
}
