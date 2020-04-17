package com.columnzero.gstruct.lang.grammar;

import groovy.lang.Closure;

import java.util.Map;

public interface StructSpec extends DocumentationSpec {
    void field(Map<String, Closure<FieldSpec>> fieldDef);
    void inherit(RefSpec id);
}
