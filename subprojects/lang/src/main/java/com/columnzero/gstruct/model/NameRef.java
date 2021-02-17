package com.columnzero.gstruct.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
public class NameRef<T> implements Ref<T> {

    public static <T> NameRef<T> named(String name, Ref<T> typeRef) {
        return new NameRef<>(name, typeRef);
    }

    public static <T> NameRef<T> named(String name, T type) {
        return new NameRef<>(name, Ref.constRef(type));
    }

    @NonNull String name;

    @Getter(AccessLevel.PRIVATE)
    @NonNull Ref<T> typeRef;

    @Override
    public T apply() {
        return typeRef.get();
    }

    @Override
    public String toString() {
        return "NameRef{" + name + ":" + typeRef.get() + "}";
    }
}
