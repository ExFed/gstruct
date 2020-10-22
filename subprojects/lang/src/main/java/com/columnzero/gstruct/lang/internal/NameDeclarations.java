package com.columnzero.gstruct.lang.internal;

import com.columnzero.gstruct.lang.grammar.DocumentationSpec;
import com.columnzero.gstruct.lang.grammar.FieldSpec;
import com.columnzero.gstruct.lang.grammar.FileSpec;
import com.columnzero.gstruct.lang.grammar.PackageSpec;
import com.columnzero.gstruct.lang.grammar.RefSpec;
import com.columnzero.gstruct.lang.grammar.StructSpec;
import groovy.lang.Closure;
import org.tinylog.Logger;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class NameDeclarations implements FieldSpec,
                                         FileSpec,
                                         PackageSpec,
                                         StructSpec,
                                         DocumentationSpec {
    // an empty value
    private static final Object EMPTY = new Object();

    // a closure that returns an empty value
    private static final Closure<Object> EMPTY_CLOSURE = new Closure<>(null) {
        @Override
        public Object call(Object... args) {
            return EMPTY;
        }
    };

    private static final Set<String> KEYWORDS = Set.of("typedef", "struct", "field");

    private final Set<String> $names = new LinkedHashSet<>();

    public NameDeclarations() {
    }

    public Set<String> $names() {
        return Collections.unmodifiableSet($names);
    }

    @Override
    public void typedef(Map<String, Closure<RefSpec>> typeDef) {
        addNames(typeDef.keySet());
    }

    @Override
    public void struct(Map<String, Closure<StructSpec>> structDef) {
        addNames(structDef.keySet());
    }

    @Override
    public void field(Map<String, Closure<FieldSpec>> fieldDef) {
        addNames(fieldDef.keySet());
    }

    @Override
    public void include(String included) {
        // noop
    }

    @Override
    public void type(RefSpec spec) {
        // noop
    }

    @Override
    public void setDescription(String description) {
        // noop
    }

    private void addNames(Set<String> names) {
        for (String name : names) {
            if (!this.$names.add(name)) {
                Logger.warn("Duplicate name declared: {}", name);
            }
        }
    }

    // ignores RHS expressions
    @SuppressWarnings("unused")
    private Object methodMissing(String methodName, Object argsObj) {
        return EMPTY_CLOSURE;
    }

    // ignores RHS expressions
    @SuppressWarnings("unused")
    private Object propertyMissing(String name) {
        return EMPTY_CLOSURE;
    }
}
