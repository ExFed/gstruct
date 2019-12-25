package com.columnzero.gstruct.graph

import com.columnzero.gstruct.FQName

import groovy.transform.*

@Canonical
class Graph {
    final def triples = [] as LinkedHashSet

    public Graph put(Triple triple) {
        triples << triple
        return this
    }

    public Graph put(FQName subject, FQName predicate, Object object) {
        return put(new Triple(subject, predicate, object))
    }
}
