package com.columnzero.gstruct.model;

import com.columnzero.gstruct.model.Identifier.Local;
import io.vavr.Tuple;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.stream.Collectors;

import static com.columnzero.gstruct.model.Identifier.local;
import static com.columnzero.gstruct.model.Type.narrow;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class Struct implements Type {

    private static final Struct UNIT = new Struct(LinkedHashMap.empty());

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Constructs a struct.
     *
     * @param fields a map of field names to field types
     *
     * @return a new struct
     */
    public static Struct struct(java.util.Map<String, ? extends Ref<? extends Type>> fields) {

        final Map<Local, Type> locals =
                LinkedHashMap.ofAll(fields).map((k, v) -> Tuple.of(local(k), narrow(v)));
        return new Struct(locals);
    }

    /**
     * Constructs the "unit struct" that has no fields.
     *
     * @return the unit struct
     */
    public static Struct struct() {
        return UNIT;
    }

    @NonNull Map<Local, Type> fields;

    @Override
    public String toString() {
        return fields.map(entry -> entry._1() + ":" + entry._2())
                     .collect(Collectors.joining(", ", "Struct(", ")"));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        private Map<Local, Type> fields = LinkedHashMap.empty();

        public Builder field(String id, Type type) {
            return field(local(id), type);
        }

        public Builder field(Local id, Type type) {
            fields = fields.put(id, type);
            return this;
        }

        public Struct build() {
            return new Struct(fields);
        }
    }
}
