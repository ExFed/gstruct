package com.columnzero.gstruct.model;

import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class Struct implements Type {

    @lombok.Builder
    public static Struct struct(@Singular java.util.Map<String, ? extends Ref<? extends Type>> fields) {
        final var lhm = LinkedHashMap.ofAll(fields);
        return new Struct(lhm.mapValues(Ref::narrow));
    }

    @Singular
    @NonNull Map<String, Ref<Type>> fields;

    @Override
    public String toString() {
        return fields.map(entry -> entry._1() + ":" + entry._2())
                     .collect(Collectors.joining(", ", "Struct(", ")"));
    }
}
