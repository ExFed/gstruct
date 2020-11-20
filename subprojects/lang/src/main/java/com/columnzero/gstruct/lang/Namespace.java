package com.columnzero.gstruct.lang;

import com.columnzero.gstruct.util.Path;
import com.columnzero.gstruct.util.Paths;
import lombok.NonNull;
import lombok.Value;

import java.io.File;

@Value(staticConstructor = "namespace")
public class Namespace {

    public static Namespace from(String... path) {
        return namespace(Path.path(path));
    }

    public static Namespace from(File rootDir, File src) {
        return new Namespace(Paths.from(src.getParentFile(), rootDir));
    }

    @NonNull Path<String> path;

    public Namespace getParent() {
        return Namespace.namespace(path.getParent());
    }
}
