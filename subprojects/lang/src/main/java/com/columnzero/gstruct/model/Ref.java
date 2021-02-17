package com.columnzero.gstruct.model;

import io.vavr.Function0;
import org.codehaus.groovy.util.HashCodeHelper;

import java.util.Objects;
import java.util.function.Supplier;

public interface Ref<T> extends Function0<T> {

    static <R> Ref<R> constRef(R value) {
        return ref(() -> value);
    }

    static <R> Ref<R> ref(Supplier<R> supplier) {
        return new Ref<>() {
            @Override
            public R apply() {
                return supplier.get();
            }

            @Override
            public int hashCode() {
                int hash;
                hash = HashCodeHelper.initHash();
                hash = HashCodeHelper.updateHash(hash, this.get());
                return hash;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }

                // implicit null check
                if (!(obj instanceof Ref)) {
                    return false;
                }

                Ref<?> other = (Ref<?>) obj;

                return Objects.equals(this.get(), other.get());
            }

            @Override
            public String toString() {
                return "Ref{" + get().toString() + "}";
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <R> Ref<R> narrow(Ref<? extends R> ref) {
        return (Ref<R>) ref;
    }

    static <R> Ref<R> eager(Ref<R> ref) {
        return constRef(ref.get());
    }

    static <R> Ref<R> lazy(Ref<R> ref) {
        // idempotent
        return ref.isMemoized() ? ref : ref.memoized()::get;
    }
}
