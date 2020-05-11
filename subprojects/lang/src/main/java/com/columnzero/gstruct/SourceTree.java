package com.columnzero.gstruct;

import java.io.File;
import java.util.Collection;

/**
 * A tree of source files.
 */
public interface SourceTree {
    /**
     * Gets source files contained within the source tree.
     *
     * @return A collection of files.
     */
    Collection<File> getFiles();
}
