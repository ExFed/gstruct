package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Local;
import io.vavr.Tuple;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.stream.Collectors;

import static com.columnzero.gstruct.model.Identifier.local;
import static com.columnzero.gstruct.model.Ref.narrow;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class Struct implements Type {

    @lombok.Builder
    public static Struct struct(@Singular java.util.Map<String, ? extends Ref<? extends Type>> fields) {
        final Map<Local, Ref<Type>> locals =
                LinkedHashMap.ofAll(fields).map((k, v) -> Tuple.of(local(k), narrow(v)));
        return new Struct(locals);
    }

    @Singular
    @NonNull Map<Local, Ref<Type>> fields;

    @Override
    public String toString() {
        return fields.map(entry -> entry._1() + ":" + entry._2())
                     .collect(Collectors.joining(", ", "Struct(", ")"));
    }
}
