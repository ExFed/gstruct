package com.columnzero.gstruct.lang;

import com.columnzero.gstruct.util.Path;
import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "namespace")
public class Namespace {

    public static Namespace from(String... path) {
        return namespace(Path.path(path));
    }

    @NonNull Path<String> path;

    public Namespace getParent() {
        return Namespace.namespace(path.getParent());
    }
}
