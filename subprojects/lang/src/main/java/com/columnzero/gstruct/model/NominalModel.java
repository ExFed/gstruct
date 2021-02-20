package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Name;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import io.vavr.collection.TreeMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.columnzero.gstruct.model.Identifier.name;

@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public final class NominalModel {

    public static NominalModel of(Iterable<NameRef> nameRefs) {
        final var model = new NominalModel();
        Stream.ofAll(nameRefs).forEach(nr -> model.bind(nr.getName(), Ref.constRef(nr.get())));
        return model;
    }

    @Getter
    private @NonNull Map<Name, Ref<Type>> bindings = TreeMap.empty();

    public Set<NameRef> getNameRefs() {
        return bindings.keySet().map(name -> NameRef.of(name, this));
    }

    public Binder bind(Type type) {
        return new Binder(Ref.constRef(type));
    }

    public Binder bind(Ref<? extends Type> type) {
        return new Binder(Ref.narrow(type));
    }

    public NameRef bind(Name name, Type type) {
        return bind(name, Ref.constRef(type));
    }

    public NameRef bind(Name name, Ref<? extends Type> typeRef) {
        if (bindings.containsKey(name)) {
            throw new BindingException("cannot bind duplicate name: " + name);
        }
        bindings = bindings.put(name, Ref.narrow(typeRef));
        return ref(name);
    }

    public NameRef ref(Name name) {
        return NameRef.of(name, this);
    }

    @Override
    public String toString() {
        final var bindingsString =
                bindings.map(binding -> binding.apply((name, typeRef) -> name + ":" + typeRef))
                        .map(Objects::toString)
                        .collect(Collectors.joining(", "));
        return "NominalModel(" + bindingsString + ")";
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(AccessLevel.NONE)
    @Value
    public class Binder {

        @NonNull Ref<Type> type;

        public NameRef to(Name name) {
            return bind(name, type);
        }

        public NameRef to(String... path) {
            return bind(name(path), type);
        }
    }

    public static class BindingException extends RuntimeException {
        private BindingException(String message) {
            super(message);
        }
    }

    public static class BindingNotFoundException extends RuntimeException {
        private BindingNotFoundException(Name name) {
            super("name not found: " + name);
        }
    }
}
