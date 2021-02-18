package com.columnzero.gstruct.model;

import com.columnzero.gstruct.util.Path;
import com.columnzero.gstruct.util.Comparators;
import io.vavr.collection.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.stream.Collectors;

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
            return Comparators.lexicographic(this.path, that.path);
        }

        @Override
        public String toString() {
            return Stream.ofAll(path).map(Local::getId).collect(Collectors.joining("/"));
        }
    }
}
