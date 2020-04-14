package com.columnzero.gstruct.graph

import com.columnzero.gstruct.lang.FQName
import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
@Canonical
class Graph {
    final Set<Triple> triples = [] as LinkedHashSet

    public Graph put(Triple triple) {
        triples << triple
        return this
    }

    public Graph put(FQName subject, FQName predicate, Object object) {
        return put(new Triple(subject, predicate, object))
    }
}
