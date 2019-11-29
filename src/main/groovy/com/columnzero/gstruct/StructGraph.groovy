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

    public StructGraph put(CName subject, CName predicate, Object object) {
        triples << new GraphTriple(subject, predicate, object)
        return this
    }
}

@Canonical
class GraphContext {
    final StructGraph graph
    final CName name

    GraphContext scope(CName name) {
        return new GraphContext(this.graph, name)
    }

    public GraphContext put(CName predicate, CName object) {
        graph.put(name, predicate, object)
        return this
    }

    public GraphContext putStr(CName predicate, String body) {
        graph.put(name, predicate, body)
        return this
    }
}