package com.columnzero.gstruct.model;

import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class Tuple implements Type {

    private static final Tuple UNIT = new Tuple(List.empty());

    /**
     * Constructs a tuple.
     *
     * @param types an ordered sequence of types
     *
     * @return a new tuple
     */
    @lombok.Builder
    public static Tuple tuple(@Singular Iterable<Ref<Type>> types) {
        return new Tuple(Array.ofAll(types));
    }

    /**
     * Constructs a tuple.
     *
     * @param types an ordered sequence of types
     *
     * @return a new tuple
     */
    public static Tuple tuple(Type... types) {
        return new Tuple(Array.of(types).map(Ref::constRef));
    }

    /**
     * Constructs a tuple.
     *
     * @param refs an ordered sequence of type refs
     *
     * @return a new tuple
     */
    @SafeVarargs
    public static Tuple tuple(Ref<? extends Type>... refs) {
        final Array<Ref<Type>> narrowed = Array.of(refs).map(Ref::narrow);
        return new Tuple(narrowed);
    }

    /**
     * Constructs the "unit tuple" that has no elements.
     *
     * @return the zero-tuple
     */
    public static Tuple tuple() {
        return UNIT;
    }

    @NonNull Seq<Ref<Type>> types;

    @Override
    public String toString() {
        return types.map(Object::toString)
                    .collect(Collectors.joining(", ", "Tuple(", ")")) + ")";
    }
}
