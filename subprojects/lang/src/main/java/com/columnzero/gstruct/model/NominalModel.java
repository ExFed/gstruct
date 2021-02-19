package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Name;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.collection.TreeMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public final class NominalModel {

    public static NominalModel of(Iterable<NameRef<Type>> nameRefs) {
        final var model = new NominalModel();
        Stream.ofAll(nameRefs).forEach(nr -> model.bind(nr.getName(), Ref.constRef(nr.get())));
        return model;
    }

    @Getter
    private @NonNull Map<Name, Ref<Type>> bindings = TreeMap.empty();

    public void bind(Name name, Ref<Type> typeRef) {
        if (bindings.containsKey(name)) {
            throw new BindingException("cannot bind duplicate name: " + name);
        }
        bindings = bindings.put(name, typeRef);
    }

    @Override
    public String toString() {
        final var bindingsString =
                bindings.map(binding -> binding.apply((name, typeRef) -> name + ":" + typeRef))
                        .map(Objects::toString)
                        .collect(Collectors.joining(", "));
        return "NominalModel(" + bindingsString + ")";
    }

    public static class BindingException extends RuntimeException {
        private BindingException(String message) {
            super(message);
        }
    }
}
