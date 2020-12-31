package com.columnzero.gstruct.lang.grammar;

import groovy.lang.Closure;

import java.util.Map;

public interface Struct extends Documented {
    void field(Map<String, Closure<Field>> fieldDef);
}
