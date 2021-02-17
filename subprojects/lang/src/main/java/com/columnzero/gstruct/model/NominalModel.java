package com.columnzero.gstruct.model;

import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.collection.TreeMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.stream.Collectors;

import static com.columnzero.gstruct.model.NameRef.named;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@ToString
public class NominalModel {

    public static NominalModel of(java.util.Map<String, Ref<Type>> map) {
        return new NominalModel(TreeMap.ofAll(map));
    }

    public static NominalModel of(Iterable<NameRef<Type>> nameRefs) {
        final var model = new NominalModel();
        Stream.ofAll(nameRefs).forEach(nr -> model.bind(nr.getName(), nr.get()));
        return model;
    }

    private @NonNull Map<String, Ref<Type>> bindings = TreeMap.empty();

    public java.util.Map<String, NameRef<Type>> getNamedRefs() {
        return bindings.toStream()
                       .collect(Collectors.toMap(Tuple2::_1, e -> named(e._1(), e._2())));
    }

    public void bind(java.util.Map<String, Ref<Type>> map) {
        map.forEach(this::bind);
    }

    public void bind(String name, Ref<Type> typeRef) {
        if (bindings.containsKey(name)) {
            throw new BindingException("cannot bind duplicate name: " + name);
        }
        bindings = bindings.put(name, typeRef);
    }

    public void bind(String name, Type type) {
        this.bind(name, Ref.constRef(type));
    }

    public static class BindingException extends RuntimeException {
        private BindingException(String message) {
            super(message);
        }
    }
}
