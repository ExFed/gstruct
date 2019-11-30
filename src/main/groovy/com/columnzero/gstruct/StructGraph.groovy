package com.columnzero.gstruct

import groovy.transform.*

@Canonical
class GraphTriple {
    Object subject
    Object predicate
    Object object

    @Override
    String toString() {
        return "<$subject><$predicate><$object>"
    }
}

@Canonical
class StructGraph {
    final def triples = [] as Set

    public StructGraph put(FQName subject, FQName predicate, Object object) {
        triples << new GraphTriple(subject, predicate, object)
        return this
    }
}
