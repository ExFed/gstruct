package com.columnzero.gstruct.lang.internal;

import com.columnzero.gstruct.lang.grammar.Documented;
import com.columnzero.gstruct.lang.grammar.Field;
import com.columnzero.gstruct.lang.grammar.File;
import com.columnzero.gstruct.lang.grammar.Package;
import com.columnzero.gstruct.lang.grammar.RefSpec;
import com.columnzero.gstruct.lang.grammar.Struct;
import com.columnzero.gstruct.util.FQName;
import groovy.lang.Closure;
import org.tinylog.Logger;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class NameDeclarations implements File, Package, Struct, Documented {
    // an empty value
    private static final Object EMPTY = new Object();

    // a closure that returns an empty value
    private static final Closure<Object> EMPTY_CLOSURE = new Closure<>(null) {
        @Override
        public Object call() {
            return EMPTY;
        }

        @Override
        public Object call(Object arguments) {
            return EMPTY;
        }

        @Override
        public Object call(Object... args) {
            return EMPTY;
        }
    };

    private final Set<String> $names = new LinkedHashSet<>();

    public NameDeclarations() {
    }

    public Set<String> $names() {
        return Collections.unmodifiableSet($names);
    }

    public RefSpec primitive(Closure<?> def) {
        return getPrimitive();
    }

    public RefSpec getPrimitive() {
        return () -> FQName.of("primitive");
    }

    @Override
    public void struct(Map<String, Closure<Struct>> structDef) {
        addNames(structDef.keySet());
    }

    @Override
    public void field(Map<String, Closure<Field>> fieldDef) {
        addNames(fieldDef.keySet());
    }

    @Override
    public void using(Map<String, RefSpec> aliases) {
        $names.addAll(aliases.keySet());
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
