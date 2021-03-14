package com.columnzero.gstruct;

import com.columnzero.gstruct.util.Path;
import com.columnzero.gstruct.util.Paths;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.io.File;

/**
 * Represents a source file.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class SourceFile implements Comparable<SourceFile> {

    /**
     * Creates a new {@link SourceFile} instance.
     *
     * @param root Root of the file tree containing this source file
     * @param file The source file
     *
     * @return a new {@link SourceFile}
     */
    public static SourceFile sourceFile(@NonNull SourceTree.Root root, @NonNull File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("not a regular file: " + file);
        }

        return new SourceFile(root, file);
    }

    @NonNull SourceTree.Root root;
    @NonNull File file;

    /**
     * Gets the namespace of this source file relative to a given root directory.
     *
     * @return The namespace of the file.
     */
    public Path<String> getNamespace() {
        return Paths.from(this.file.getParentFile(), root.getDirectory().toFile());
    }

    @Override
    public int compareTo(SourceFile o) {
        return this.getFile().compareTo(o.getFile());
    }
}
