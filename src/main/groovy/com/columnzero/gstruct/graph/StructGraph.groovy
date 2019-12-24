package com.columnzero.gstruct.graph

import com.columnzero.gstruct.FQName

import groovy.transform.*

@Canonical
class StructGraph {
    final def triples = [] as LinkedHashSet

    public StructGraph put(Triple triple) {
        return put(triple.subject, triple.predicate, triple.object)
    }

    public StructGraph put(FQName subject, FQName predicate, Object object) {
        triples << new Triple(subject, predicate, object)
        return this
    }
}
