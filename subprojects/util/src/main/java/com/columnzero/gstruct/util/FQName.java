package com.columnzero.gstruct.util;

import java.util.Arrays;

import io.vavr.collection.Stream;
import lombok.NonNull;
import lombok.Value;

@Value
public class FQName implements Comparable<FQName> {
    public static FQName of(Path<String> path) {
        if (path.getDepth() < 1) {
            throw new IllegalArgumentException("Path must have at least one element");
        }

        return new FQName(path.getValue(), path.getParent());
    }

    public static FQName of(String pathHead, String... pathTail) {
        final var path = Path.of(pathHead).child(Arrays.asList(pathTail));
        return new FQName(path.getValue(), path.getParent());
    }

    @NonNull String name;
    @NonNull Path<String> namespace;

    @Override
    public String toString() {
        return namespace.toString("/") + "/" + name;
    }

    @Override
    public int compareTo(FQName that) {
        final var these = Stream.concat(this.getNamespace(), Stream.of(this.getName()));
        final var those = Stream.concat(that.getNamespace(), Stream.of(that.getName()));
        return compareIter(these, those);
    }

    private <E extends Comparable<E>> int compareIter(Iterable<E> a, Iterable<E> b) {
        final var aIt = a.iterator();
        final var bIt = b.iterator();
        while (aIt.hasNext() && bIt.hasNext()) {
            final var comparison = aIt.next().compareTo(bIt.next());
            if (0 != comparison) {
                return comparison;
            }
        }
        return aIt.hasNext() ? 1 : bIt.hasNext() ? -1 : 0;
    }
}
