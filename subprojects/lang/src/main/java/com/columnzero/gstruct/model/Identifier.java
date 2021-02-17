package com.columnzero.gstruct.model;

import com.columnzero.gstruct.util.Path;
import io.vavr.collection.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

public interface Identifier {

    static Local local(String id) {
        return new Local(id);
    }

    static Name name(String... path) {
        final var locals = Stream.of(path).map(Identifier::local);
        return new Name(Path.of(locals));
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Value
    class Local implements Identifier, Comparable<Local> {
        @NonNull String id;

        @Override
        public int compareTo(Local that) {
            return this.id.compareTo(that.id);
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Value
    class Name implements Identifier, Comparable<Name> {
        @NonNull Path<Local> path;

        @Override
        public int compareTo(Name that) {
            return Util.lexicalCompare(this.path, that.path);
        }
    }
}
