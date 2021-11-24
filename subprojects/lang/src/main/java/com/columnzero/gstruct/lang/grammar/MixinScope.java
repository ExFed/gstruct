package com.columnzero.gstruct.lang.grammar;

import com.columnzero.gstruct.util.function.CallResult;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Tuple;
import groovy.lang.Tuple2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.tinylog.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A scope element that delegates to a sequence of scopes. Calls to {@link #invokeMethod(String,
 * Object)}, {@link #getProperty(String)}, or {@link #setProperty(String, Object)} will scan until a
 * scope has the appropriate method or property.
 */
public final class MixinScope implements GroovyObject {

    private final @NonNull List<ScopeProxy> proxies;

    @Getter
    private final @NonNull MetaClass metaClass = InvokerHelper.getMetaClass(MixinScope.class);

    @lombok.Builder
    public MixinScope(@Singular @NonNull List<Object> scopes) {
        this.proxies = scopes.stream()
                             .map(ScopeProxy::new)
                             .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        var argArray = InvokerHelper.asArray(args);

        for (var proxy : proxies) {
            var result = proxy.invokeUnlessMissing(name, argArray);
            if (result.isSuccess()) {
                return result.getValue();
            }
        }

        // none of the inner scopes responded to this method
        throw new MissingMethodException(name, this.getClass(), argArray);
    }

    private Tuple2<MetaProperty, ScopeProxy> findProperty(String name) {
        for (var proxy : proxies) {
            var property = proxy.getMetaClass().hasProperty(proxy.getScope(), name);
            if (property != null) {
                return Tuple.tuple(property, proxy);
            }
        }
        return null;
    }

    @Override
    public Object getProperty(String id) {
        for (var proxy : proxies) {
            try {
                return proxy.getProperty(id);
            } catch (MissingPropertyException ex) {
                Logger.trace(ex);
                // continue
            }
        }
        throw new MissingPropertyException(id, this.getClass());
    }

    @Override
    public void setProperty(String id, Object value) {
        for (var proxy : proxies) {
            try {
                proxy.setProperty(id, value);
                return;
            } catch (MissingPropertyException ex) {
                Logger.trace(ex);
                // continue
            }
        }
        throw new MissingPropertyException(id, this.getClass());
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        throw new UnsupportedOperationException("cannot set metaclass");
    }
}

@AllArgsConstructor
@Getter
final class ScopeProxy implements GroovyObject {

    private final @NonNull Object scope;
    private final @NonNull MetaClass metaClass;

    public ScopeProxy(Object scope) {
        this(scope, InvokerHelper.getMetaClass(scope));
    }

    public Object invokeMethod(String id, Object args) {
        return metaClass.invokeMethod(scope, id, args);
    }

    @Override
    public Object getProperty(String id) {
        return metaClass.getProperty(scope, id);
    }

    @Override
    public void setProperty(String id, Object newValue) {
        metaClass.setProperty(scope, id, newValue);
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        throw new UnsupportedOperationException("cannot set metaclass");
    }

    public CallResult<Object> invokeUnlessMissing(String name, Object[] args) {
        try {
            return CallResult.success(this.invokeMethod(name, args));
        } catch (MissingMethodException ex) {
            return CallResult.failure(ex);
        }
    }
}
