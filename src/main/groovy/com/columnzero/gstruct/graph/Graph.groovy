package com.columnzero.gstruct.graph

import com.columnzero.gstruct.FQName

import groovy.transform.*

@Canonical
class Graph {
    final def triples = [] as LinkedHashSet

    public Graph put(Triple triple) {
        return put(triple.subject, triple.predicate, triple.object)
    }

    public Graph put(FQName subject, FQName predicate, Object object) {
        triples << new Triple(subject, predicate, object)
        return this
    }
}
