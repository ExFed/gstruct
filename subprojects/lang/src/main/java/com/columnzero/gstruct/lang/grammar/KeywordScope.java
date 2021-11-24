package com.columnzero.gstruct.lang.grammar;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import lombok.NonNull;
import lombok.Value;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public interface KeywordScope extends GroovyObject {

    static KeywordScope scope(Map<String, Object> keywords) {
        return new DefaultKeywordScope(new HashMap<>(keywords));
    }

    @Override
    default MetaClass getMetaClass() {
        return MetaClassCache.getMetaClass(this.getClass());
    }

    @Override
    default void setMetaClass(MetaClass metaClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object invokeMethod(String name, Object args) {
        var property = this.getProperty(name);
        var argArray = InvokerHelper.asArray(args);
        if (property instanceof Closure) {
            Closure<?> cl = (Closure<?>) property;
            Closure<?> clonedClosure = cl.rehydrate(this, cl.getOwner(), this);
            clonedClosure.setResolveStrategy(Closure.DELEGATE_ONLY);
            return clonedClosure.call(argArray);
        } else {
            throw new MissingMethodException(name, this.getClass(), argArray);
        }
    }

    @Override
    default Object getProperty(String propertyName) {
        return get(propertyName).orElseThrow(() -> {
            throw new MissingPropertyException("property not found: " + propertyName);
        });
    }

    @Override
    default void setProperty(String propertyName, Object newValue) {
        throw new UnsupportedOperationException("cannot assign readonly property: " + propertyName);
    }

    Map<String, Object> getKeywords();

    Optional<Object> get(String keyword);
}

class MetaClassCache {

    private static final Map<Class<?>, MetaClass> CACHE = new ConcurrentHashMap<>();

    public static MetaClass getMetaClass(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, InvokerHelper::getMetaClass);
    }
}

@Value
class DefaultKeywordScope implements KeywordScope {

    @NonNull Map<String, Object> keywords;

    @Override
    public Optional<Object> get(String keyword) {
        return Optional.ofNullable(keywords.get(keyword));
    }
}
