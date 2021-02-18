package com.columnzero.gstruct.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.Objects;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Value
class DefaultRef<R> implements Ref<R> {

    @Getter(AccessLevel.NONE)
    @NonNull Supplier<R> supplier;

    @Override
    public R apply() {
        return supplier.get();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(supplier.get());
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
}
