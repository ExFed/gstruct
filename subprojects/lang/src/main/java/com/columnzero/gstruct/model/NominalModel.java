package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Name;
import com.columnzero.gstruct.model.Type.Ref;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.TreeMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.columnzero.gstruct.model.Identifier.name;

@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public final class NominalModel {

    /**
     * Map of fully-qualified names bound to type references.
     */
    @Getter
    private @NonNull Map<Name, Ref<? extends Type>> bindings = TreeMap.empty();

    /**
     * @return a set of all {@linkplain NameRef NameRefs} accumulated by this model's bindings
     */
    public Set<NameRef> getNameRefs() {
        return bindings.keySet().map(name -> NameRef.of(name, this));
    }

    public Binder bind(Type type) {
        return new Binder(type);
    }

    /**
     * Binds a name to a type reference.
     *
     * @param name identifier of the type to bind
     * @param type the type being bound
     *
     * @return the resulting {@link NameRef} representing the binding
     */
    public NameRef bind(@NonNull Name name, @NonNull Type type) {
        if (bindings.containsKey(name)) {
            throw new DuplicateBindingException("cannot bind duplicate name: " + name);
        }
        if (type instanceof Ref) {
            var typeRef = (Ref<? extends Type>) type;
            bindings = bindings.put(name, typeRef);
        } else {
            bindings = bindings.put(name, Type.constRef(type));
        }
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
    public final class Binder {

        private final @NonNull Type type;

        public NameRef to(Name name) {
            return bind(name, type);
        }

        public NameRef to(String... path) {
            return bind(name(path), type);
        }
    }

}
