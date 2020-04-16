package com.columnzero.gstruct.lang.grammar;

import groovy.lang.Closure;

import java.util.Map;

public interface PackageSpec extends DocumentationSpec {
    void type(Map<String, IdentifierSpec> typeDef);
    void struct(Map<String, Closure<StructSpec>> structDef);
}
