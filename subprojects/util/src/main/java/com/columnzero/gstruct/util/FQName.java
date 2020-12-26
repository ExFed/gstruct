package com.columnzero.gstruct.util;

import java.util.ArrayList;
import java.util.Arrays;

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

    public static FQName of(String rootName, String... tokens) {
        final var path = Path.of(rootName).child(Arrays.asList(tokens));
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
        final var thisNsIter = this.getNamespace().iterator();
        final var thatNsIter = that.getNamespace().iterator();

        int cmp;
        while (thisNsIter.hasNext() && thatNsIter.hasNext()) {
            cmp = thisNsIter.next().compareTo(thatNsIter.next());
            if (0 != cmp) {
                return cmp;
            }
        }

        // final var thisEndNode = thisNsIter.hasNext() ? thisNsIter.next() : this.getName();
        // final var thatEndNode = thatNsIter.hasNext() ? thatNsIter.next() : that.getName();

        if (thisNsIter.hasNext()) {
            cmp = thisNsIter.next().compareTo(that.getName());
            return 0 != cmp ? cmp : 1;
        } else if (thatNsIter.hasNext()) {
            cmp = this.getName().compareTo(thatNsIter.next());
            return 0 != cmp ? cmp : -1;
        } else {
            return this.getName().compareTo(that.getName());
        }
    }
}
