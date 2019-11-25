package com.columnzero.gstruct

import groovy.transform.*

@Immutable
class GraphTriple {
    CName subject
    CName predicate
    CName object
}

@EqualsAndHashCode
@ToString
class StructGraph {
    private final def triples = []

    public StructGraph put(CName subject, CName predicate, CName object) {
        triples << new GraphTriple(subject, predicate, object)
        return this
    }

    public Map getSopIndex() {
        // [ Subject : [ Object : [PredicateSet] ] ]
        def sopIndex = [:].withDefault {[:].withDefault {[] as Set}}
        triples.each { sopIndex[it.subject][it.object] << it.predicate }
        return sopIndex
    }
}
