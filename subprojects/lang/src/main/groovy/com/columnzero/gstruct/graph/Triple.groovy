package com.columnzero.gstruct.graph

import com.columnzero.gstruct.glang.FQName
import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
@Canonical
class Triple {
    final FQName subject
    final FQName predicate
    final Object object

    @Override
    String toString() {
        return "<$subject> <$predicate> <$object>"
    }

    def asType(Class clazz) {
        switch(clazz) {
            case List:
                return [subject, predicate, object]
            case String:
                return toString()
        }

        throw new ClassCastException("Cannot convert ${this.getClass()} to $clazz")
    }
}
