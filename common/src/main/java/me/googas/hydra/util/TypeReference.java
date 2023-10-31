package me.googas.hydra.util;

import lombok.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {
    private final Type type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Missing type parameter");
        }
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    @NonNull
    public Type getType() {
        return this.type;
    }
}
