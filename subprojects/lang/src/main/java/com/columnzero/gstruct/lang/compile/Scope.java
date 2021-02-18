package com.columnzero.gstruct.lang.compile;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
class Scope extends GroovyObjectSupport {

    @SuppressWarnings("unchecked")
    public static Scope of(Map<String, ?> properties) {
        return new Scope((Map<String, Object>) properties);
    }

    public static Scope inherit(Scope... scopes) {
        Map<String, Object> keywords = new HashMap<>();
        for (Scope scope : scopes) {
            keywords.putAll(scope.getKeywords());
        }
        return new Scope(keywords);
    }

    @Getter
    private final @NonNull Map<String, Object> keywords;

    public Scope() {
        this(new HashMap<>());
    }

    @Override
    public Object getProperty(String property) {
        if (!keywords.containsKey(property)) {
            throw new MissingPropertyException("property not found: " + property);
        }
        return keywords.get(property);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        throw new UnsupportedOperationException("cannot assign readonly property: " + property);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        var value = this.getProperty(name);
        if (value instanceof Closure) {
            Closure<?> cl = (Closure<?>) value;
            Closure<?> clonedClosure = cl.rehydrate(this, cl.getOwner(), this);
            clonedClosure.setResolveStrategy(Closure.DELEGATE_ONLY);
            return clonedClosure.call((Object[]) args);
        } else {
            Object[] argArray = args instanceof Object[]
                    ? (Object[]) args
                    : new Object[]{args};
            throw new MissingMethodException(name, Scope.class, argArray);
        }
    }

    @Override
    public String toString() {
        return "Scope(" + keywords + ')';
    }
}
