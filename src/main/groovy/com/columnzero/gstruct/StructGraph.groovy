package com.columnzero.gstruct

import groovy.transform.*

@Immutable
class GraphTriple {
    CName subject
    CName predicate
    CName object
}

class StructGraph {
    final static StructGraph sg = new StructGraph()

    static void edge(CName subject, CName predicate, CName object) {
        sg.put(subject, predicate, object)
    }

    private final def triples = []

    public void put(CName subject, CName predicate, CName object) {
        triples << new GraphTriple(subject, predicate, object)
    }

    public Map getSop() {
        // [ Subject : [ Object : [PredicateSet] ] ]
        def sopIndex = [:].withDefault {[:].withDefault {[] as Set}}
        triples.each { sopIndex[it.subject][it.object] << it.predicate }
        return sopIndex
    }
}
