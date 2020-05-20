package com.columnzero.gstruct.util;

import java.io.File;

/**
 * A utility class for managing paths.
 */
public class Paths {

    private Paths() {
        throw new UnsupportedOperationException("utility class");
    }

    /**
     * Creates a {@link Path} from a {@link java.nio.file.Path}.
     *
     * @param nioPath Java NIO path to use.
     *
     * @return A new path.
     */
    public static Path<String> from(java.nio.file.Path nioPath) {
        if (null == nioPath
                || 0 == nioPath.getNameCount()
                || nioPath.normalize().toString().isEmpty()) {
            return Path.getRoot();
        }
        return from(nioPath.getParent()).child(nioPath.getFileName().toString());
    }

    /**
     * Creates a {@link Path} between two files.
     *
     * @param file     File to use.
     * @param ancestor Ancestor of the file.
     *
     * @return A new path.
     */
    public static Path<String> from(File file, File ancestor) {
        final java.nio.file.Path filePath = file.toPath().toAbsolutePath().normalize();
        final java.nio.file.Path ancestorPath = ancestor.toPath().toAbsolutePath().normalize();
        return from(ancestorPath.relativize(filePath));
    }
}
