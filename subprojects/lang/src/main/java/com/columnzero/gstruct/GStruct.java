package com.columnzero.gstruct;

/**
 * The main interface into GStruct.
 */
public interface GStruct {
    /**
     * Compiles a tree of source files into a type graph.
     *
     * @param sourceTree A tree of source files.
     *
     * @return A new type graph.
     */
    TypeGraph compile(SourceTree sourceTree);
}
