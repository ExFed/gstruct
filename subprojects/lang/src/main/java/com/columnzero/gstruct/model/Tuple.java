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
    public static Tuple tuple(@Singular Iterable<Type> types) {
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
        return new Tuple(Array.of(types));
    }

    /**
     * Constructs the "unit tuple" that has no elements.
     *
     * @return the zero-tuple
     */
    public static Tuple tuple() {
        return UNIT;
    }

    @NonNull Seq<Type> types;

    @Override
    public String toString() {
        return types.map(Object::toString)
                    .collect(Collectors.joining(", ", "Tuple(", ")")) + ")";
    }
}
