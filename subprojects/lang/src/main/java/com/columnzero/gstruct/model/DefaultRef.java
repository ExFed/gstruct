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
        return 31 * Objects.hashCode(supplier.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        // implicit null check
        if (!(obj instanceof DefaultRef)) {
            return false;
        }

        DefaultRef<?> that = (DefaultRef<?>) obj;

        return Objects.equals(this.get(), that.get());
    }

    @Override
    public String toString() {
        return Ref.toString(this);
    }
}
