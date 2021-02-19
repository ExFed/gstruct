package com.columnzero.gstruct.model;

import io.vavr.Function0;

import java.util.Objects;
import java.util.function.Supplier;

public interface Ref<T> extends Function0<T> {

    static <R> Ref<R> constRef(R value) {
        return ref(() -> value);
    }

    static <R> Ref<R> ref(Supplier<R> supplier) {
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
    static <R> Ref<R> narrow(Ref<? extends R> ref) {
        return (Ref<R>) ref;
    }

    static boolean equals(Ref<?> self, Object obj) {
        if (self == obj) {
            return true;
        }

        // implicit null check
        if (!(obj instanceof Ref)) {
            return false;
        }

        Ref<?> other = (Ref<?>) obj;

        return Objects.equals(self.get(), other.get());
    }

    static int hashCode(Ref<?> self) {
        return Objects.hashCode(self.get());
    }

    static String toString(Ref<?> self) {
        return "Ref->" + self.get().toString();
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}

