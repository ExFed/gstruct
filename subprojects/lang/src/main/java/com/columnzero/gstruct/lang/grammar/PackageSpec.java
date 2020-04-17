package com.columnzero.gstruct.lang.grammar;

import groovy.lang.Closure;

import java.util.Map;

public interface PackageSpec extends DocumentationSpec {
    void typedef(Map<String, Closure<RefSpec>> typeDef);
    void struct(Map<String, Closure<StructSpec>> structDef);
}
