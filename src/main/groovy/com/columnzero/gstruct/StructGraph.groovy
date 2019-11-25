package com.columnzero.gstruct

import groovy.transform.*

@Immutable
class GraphTriple {
    CName subject
    CName predicate
    CName object

    @Override
    String toString() {
        return "<$subject><$predicate><$object>"
    }
}

@Canonical
class StructGraph {
    def triples = []

    public StructGraph put(CName subject, CName predicate, CName object) {
        triples << new GraphTriple(subject, predicate, object)
        return this
    }

    public Map spoIndex() {
        // [ Subject : [ Predicate : [ Object ] ] ]
        def index = [:].withDefault {[:].withDefault {[] as Set}}
        triples.each { index[it.subject][it.predicate] << it.object }
        return index
    }
}
