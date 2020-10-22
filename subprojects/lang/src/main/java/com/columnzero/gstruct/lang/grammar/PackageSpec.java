package com.columnzero.gstruct.lang.grammar;

import groovy.lang.Closure;

import java.util.Map;

/** Specifies a package. */
public interface PackageSpec extends DocumentationSpec {

    /** Declares a typedef. */
    void typedef(Map<String, Closure<RefSpec>> typeDef);

    /** Declares a struct. */
    void struct(Map<String, Closure<StructSpec>> structDef);
}
