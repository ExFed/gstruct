package com.columnzero.gstruct.model;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.stream.Collectors;

@Value
public class Tuple implements Type {

    @lombok.Builder
    public static Tuple tuple(@Singular Iterable<Ref<Type>> types) {
        return new Tuple(List.ofAll(types));
    }

    public static Tuple tuple(Type... types) {
        return new Tuple(List.of(types).map(Ref::constRef));
    }

    @SafeVarargs
    public static Tuple tuple(Ref<? extends Type>... refs) {
        final Stream<Ref<Type>> narrowed = Stream.of(refs).map(Ref::narrow);
        return new Tuple(narrowed.toList());
    }

    @NonNull List<Ref<Type>> types;

    @Override
    public String toString() {
        return types.map(Object::toString)
                    .collect(Collectors.joining(", ", "Tuple(", ")")) + ")";
    }
}
