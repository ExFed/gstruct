package com.columnzero.gstruct.lang.grammar;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Delegate for Java objects wishing to be Groovy objects. Composition-over-inheritance analog to
 * {@link groovy.lang.GroovyObjectSupport}.
 */
@Data
final class GroovyObjectDelegate implements GroovyObject {

    @Getter(AccessLevel.NONE)
    private final @NonNull Class<?> pojoClass;

    private @NonNull MetaClass metaClass;

    public GroovyObjectDelegate(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
        this.metaClass = InvokerHelper.getMetaClass(pojoClass);
    }
}
