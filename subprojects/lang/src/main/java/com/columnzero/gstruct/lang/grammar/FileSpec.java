package com.columnzero.gstruct.lang.grammar;

/** Specifies a source file. May contain multiple specifications. */
public interface FileSpec extends PackageSpec {

    /** Declares a namespace to include. */
    void include(String namespace);
}
