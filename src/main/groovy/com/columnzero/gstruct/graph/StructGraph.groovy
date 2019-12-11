package com.columnzero.gstruct.graph

import groovy.transform.*

@Canonical
class GraphTriple {
    final Object subject
    final Object predicate
    final Object object

    @Override
    String toString() {
        return "<$subject><$predicate><$object>"
    }

    def asType(Class clazz) {
        if (clazz == List) {
            return [subject, predicate, object]
        }
        throw new ClassCastException("Cannot convert ${this.getClass()} to $clazz")
    }
}

@Canonical
class StructGraph {
    final def triples = [] as Set

    public StructGraph put(GraphTriple triple) {
        return put(triple.subject, triple.predicate, triple.object)
    }

    public StructGraph put(subject, predicate, object) {
        triples << new GraphTriple(subject, predicate, object)
        return this
    }
}
