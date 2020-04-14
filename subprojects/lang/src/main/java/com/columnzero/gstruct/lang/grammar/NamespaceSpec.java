package com.columnzero.gstruct.lang.grammar;

import groovy.lang.Closure;

import java.util.Map;

public interface NamespaceSpec extends DocumentationSpec {
    void struct(Map<String, Closure<StructSpec>> definition);
}
