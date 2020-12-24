package com.columnzero.gstruct.util;

import lombok.NonNull;
import lombok.Value;

@Value
public class FQName {
    public static FQName of(Path<String> path) {
        if (path.getDepth() < 1) {
            throw new IllegalArgumentException("Path must have at least one element");
        }

        return new FQName(path.getValue(), path.getParent());
    }

    public static FQName of(String name, String... namespace) {
        return new FQName(name, Path.of(namespace));
    }

    @NonNull String name;
    @NonNull Path<String> namespace;

    @Override
    public String toString() {
        return namespace.toString("/") + "/" + name;
    }
}
