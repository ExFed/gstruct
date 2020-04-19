package com.columnzero.gstruct.glang

import com.columnzero.gstruct.graph.Graph
import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
@Canonical
class GraphContext {
    final Graph graph
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
