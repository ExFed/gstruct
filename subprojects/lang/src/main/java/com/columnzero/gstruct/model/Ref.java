package com.columnzero.gstruct.model;

import java.util.function.Supplier;

public interface Ref<T extends Type> {

    static <R extends Type> Ref<R> constRef(R value) {
        return ref(() -> value);
    }

    static <R extends Type> Ref<R> ref(Supplier<R> supplier) {
        return new DefaultRef<>(supplier);
    }

    /**
     * Narrows the given {@code Ref<? extends R>} to {@code Ref<R>}.
     *
     * @param ref a {@code Ref}
     * @param <R> return type
     *
     * @return the given {@code ref} instance as narrowed type {@code Ref<R>}
     */
    @SuppressWarnings("unchecked")
    static <R extends Type> Ref<R> narrow(Ref<? extends R> ref) {
        return (Ref<R>) ref;
    }

    T get();
}
