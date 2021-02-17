package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Name;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

import static com.columnzero.gstruct.model.Identifier.name;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class NameRef<T> implements Ref<T>, Map.Entry<Name, Ref<T>> {

    public static <T> Of<T> of(Ref<T> ref) {
        return new Of<>(ref);
    }

    public static <T> Of<T> of(T type) {
        return new Of<>(Ref.constRef(type));
    }

    public static <T> NameRef<T> named(Name name, Ref<T> typeRef) {
        return new NameRef<>(name, typeRef);
    }

    public static <T> NameRef<T> named(Name name, T type) {
        return new NameRef<>(name, Ref.constRef(type));
    }

    @NonNull Name name;

    @NonNull Ref<T> typeRef;

    @Override
    public T apply() {
        return typeRef.get();
    }

    @Override
    public String toString() {
        return "NameRef{" + name + ":" + typeRef.get() + "}";
    }

    @Override
    public Name getKey() {
        return name;
    }

    @Override
    public Ref<T> getValue() {
        return typeRef;
    }

    @Override
    public Ref<T> setValue(Ref<T> value) {
        throw new UnsupportedOperationException("unmodifiable");
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Value
    public static class Of<T> {

        @Getter(AccessLevel.PRIVATE)
        @NonNull Ref<T> typeRef;

        public NameRef<T> named(Name name) {
            return NameRef.named(name, typeRef);
        }

        public NameRef<T> named(String... path) {
            return NameRef.named(name(path), typeRef);
        }
    }
}
