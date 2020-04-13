package com.columnzero.gstruct.query

import com.columnzero.gstruct.FQName
import com.columnzero.gstruct.graph.TripleIndex
import com.columnzero.gstruct.graph.Triple

import groovy.transform.*

/*
    QueryTree <- QueryTraversal -> Node
*/

class QueryRunner {

    static QueryRunner forGraph(TripleIndex graph) {
        return new QueryRunner(graph)
    }

    private final TripleIndex graph

    private QueryRunner(TripleIndex graph) {
        this.graph = graph
    }

    Collection<ResultTree> getResults(QueryTree query) {
        // find root nodes that satisfy the root query fields
        assert false, 'not yet implemented'
    }

    /**
     * Gets a collection of results starting at a given node.
     *
     * <pre>
     *   // given possible values of fields m
     *   m = [a:[0, 1], b:[2, 3], c:[4, 5]]
     *   // then
     *   p = m.collect{ k, v -> v.collect{ [k, it] } }
     *   // p contains objPairs of fields and values:
     *   // [
     *   //     [ [a, 0], [a, 1] ],
     *   //     [ [b, 2], [b, 3] ],
     *   //     [ [c, 4], [c, 5] ]
     *   // ]
     *   o = p.combinations().collect{ c -> c.collectEntries{ it } }
     *   // o is all object combinations of m [[a:0, b:2, c:4], [a:1, b:2, c:4], [a:0, b:3, c:4], ... ]
     * </pre>
     */
    private static Collection<ResultTree> queryNode(Node node, QueryTree query) {

        def fieldEdges = query.fields.collect{ fieldName, fieldQuery ->
            [fieldName, fieldQuery, node.edgesOut(fieldName)]
        }
        // fieldEdges := ( fieldName, fieldQuery, edges[0..*] )[0..fields.size]

        // if we can't find at least one edge for every field, this isn't the node we're looking for
        if (fieldEdges.every{ it[2].size > 0 }) {
            return []
        }
        // fieldEdges := ( fieldName, fieldQuery, edges[1..*] )[0..fields.size]

        def resultsByField = fieldEdges.collect{ fieldName, fieldQuery, edges ->
            edges.collectMany{ edge ->
                queryEdge(edge, fieldQuery).collect{ edgeResult ->
                    [fieldName, edgeResult]
                }
                // yield: (fieldName, edgeResult)[0..edgeResults.size]
            }
            // yield: (fieldName, result)[0..results.size] where results := edgeResults on all fields
        }
        // resultsByField := ( ( fieldName, result )[0..results.size] )[0..fields.size]

        return resultsByField.combinations().collect{ objPairs ->
            new ResultTree(objPairs.collectEntries{ it })
        }
    }

    // returns either a list of ResultTrees or a list of literal Objects
    private static Collection<Object> queryEdge(Edge edge, Object query) {
        def node = edge.to

        // are we looking for an object?
        if (query instanceof QueryTree) {
            if (node.isLeaf) {
                // ...and we got a literal!
                return []
            }
            return queryNode(node, query)
        }

        // must be we're looking for a literal
        return node.value
    }
}

@Canonical
class QueryTree {
    /* fieldName -> query */
    final Map<FQName, Object> fields
}

@Canonical
class ResultTree {
    final Map<FQName, Object> fields
}

@Canonical
class Node {
    final Object value
    private final TripleIndex graph

    boolean getIsLeaf() {
        return !(value instanceof FQName)
    }

    Collection<Edge> edgesOut(FQName edgeName = null) {
        return this.isLeaf ? [] : graph.findAll(s: value, p: edgeName).collect{ new Edge(it, graph) }
    }

    Collection<Edge> edgesIn(FQName edgeName = null) {
        return graph.findAll(p: edgeName, o: value).collect{ new Edge(it, graph) }
    }
}

@Canonical
class Edge {
    private final Triple triple
    private final TripleIndex graph

    FQName getName() {
        return triple.predicate
    }

    Node getFrom() {
        return new Node(triple.subject)
    }

    Node getTo() {
        return new Node(triple.object)
    }
}
