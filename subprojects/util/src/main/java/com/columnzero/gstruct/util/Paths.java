package com.columnzero.gstruct.util;

import java.io.File;
import java.util.Optional;

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
        // do some normalization and filtering up front...
        return fromRecurse(Optional.ofNullable(nioPath)
                                   .map(java.nio.file.Path::normalize)
                                   .filter(p -> !p.toString().isEmpty())
                                   .orElse(null));
    }

    /**
     * Recursively stacks parents (i.e. does post-order insertion).
     */
    private static Path<String> fromRecurse(java.nio.file.Path nioPath) {
        final Optional<java.nio.file.Path> nioPathOpt = Optional.ofNullable(nioPath);
        return nioPathOpt.map(path -> fromRecurse(path.getParent()))
                         .flatMap(parent -> nioPathOpt.map(java.nio.file.Path::getFileName)
                                                      .map(java.nio.file.Path::toString)
                                                      .map(parent::child))
                         .orElse(Path.getRoot());
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
