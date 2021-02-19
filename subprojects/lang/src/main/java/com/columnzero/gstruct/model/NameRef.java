package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Name;
import io.vavr.Function1;
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
        return new Of<>(name -> ref);
    }

    public static <T> Of<T> of(T type) {
        return new Of<>(name -> Ref.constRef(type));
    }

    public static <T> Of<T> of(Function1<Name, Ref<T>> refGetter) {
        return new Of<>(refGetter);
    }

    @NonNull Name name;

    @NonNull Function1<Name, Ref<T>> refGetter;

    @Override
    public T get() {
        return getValue().get();
    }

    @Override
    public String toString() {
        return "NameRef->" + name;
    }

    @Override
    public Name getKey() {
        return name;
    }

    @Override
    public Ref<T> getValue() {
        return refGetter.apply(name);
    }

    @Override
    public Ref<T> setValue(Ref<T> value) {
        throw new UnsupportedOperationException("unmodifiable");
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NameRef)) {
            return false;
        }
        final NameRef<?> that = (NameRef<?>) obj;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return 59 + name.hashCode();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(AccessLevel.NONE)
    @Value
    public static class Of<T> {

        @NonNull Function1<Name, Ref<T>> refGetter;

        public NameRef<T> named(Name name) {
            return new NameRef<>(name, refGetter);
        }

        public NameRef<T> named(String... path) {
            return new NameRef<>(name(path), refGetter);
        }
    }
}
