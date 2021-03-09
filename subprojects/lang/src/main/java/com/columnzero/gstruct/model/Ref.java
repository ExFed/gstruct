package com.columnzero.gstruct.model;

import io.vavr.Value;
import io.vavr.collection.Iterator;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Ref<T> extends Value<T> {

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

    @Override
    default boolean isAsync() {
        return false;
    }

    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default boolean isLazy() {
        return false;
    }

    @Override
    default boolean isSingleValued() {
        return true;
    }

    @Override
    default <U> Ref<U> map(Function<? super T, ? extends U> mapper) {
        return ref(() -> mapper.apply(get()));
    }

    @Override
    default Ref<T> peek(Consumer<? super T> action) {
        action.accept(get());
        return this;
    }

    @Override
    default String stringPrefix() {
        return "Ref";
    }

    @Override
    default Iterator<T> iterator() {
        return Iterator.of(get());
    }
}
