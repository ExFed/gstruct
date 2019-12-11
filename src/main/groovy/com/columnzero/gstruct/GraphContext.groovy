package com.columnzero.gstruct

import com.columnzero.gstruct.graph.StructGraph

import groovy.transform.*

@Canonical
class GraphContext {
    final StructGraph graph
    final FQName name

    GraphContext rescope(FQName name) {
        return new GraphContext(this.graph, name)
    }

    public GraphContext put(FQName predicate, FQName object) {
        graph.put(name, predicate, object)
        return this
    }

    public GraphContext putStr(FQName predicate, String body) {
        graph.put(name, predicate, body)
        return this
    }
}
