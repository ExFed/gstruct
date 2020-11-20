package com.columnzero.gstruct;

import com.columnzero.gstruct.lang.Namespace;
import lombok.NonNull;
import lombok.Value;

import java.io.File;

/**
 * Represents a source file.
 */
@Value(staticConstructor = "sourceFile")
public class SourceFile {

    @NonNull File file;

    public SourceFile(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("not a regular file: " + file);
        }

        this.file = file;
    }

    /**
     * Gets the namespace of this source file relative to a given root directory.
     *
     * @param rootDir Root of the file tree containing this source file.
     *
     * @return The namespace of the file.
     */
    public Namespace getNamespace(File rootDir) {
        return Namespace.from(rootDir, getFile().getParentFile());
    }
}
