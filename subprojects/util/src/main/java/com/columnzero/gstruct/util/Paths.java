package com.columnzero.gstruct.util;

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
        if (nioPath == null || 0 == nioPath.getNameCount()) {
            return Path.getRoot();
        }
        return from(nioPath.getParent()).child(nioPath.getFileName().toString());
    }
}
