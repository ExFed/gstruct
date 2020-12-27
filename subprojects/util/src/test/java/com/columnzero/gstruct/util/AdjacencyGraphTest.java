package com.columnzero.gstruct.util;

import static com.google.common.truth.Truth.assertThat;

import com.columnzero.gstruct.util.Graph.EdgeId;
import com.columnzero.gstruct.util.Graph.NodeId;

import org.junit.jupiter.api.Test;

class AdjacencyGraphTest {

    private final NodeId n1 = NodeId.of("foo", "bar");
    private final String n1Val = "node 1";

    private final String e1Val = "edge 1";

    AdjacencyGraph<String, String> givenAnEmptyGraph() {
        return AdjacencyGraph.empty();
    }

    @Test
    void hasNoNodesAndNoEdges() {
        final var graph = givenAnEmptyGraph();

        assertThat(graph.getNodes()).isEmpty();
        assertThat(graph.getEdges()).isEmpty();
    }

    AdjacencyGraph<String, String> givenOneNode() {
        return givenAnEmptyGraph().putNode(n1, n1Val);
    }

    @Test
    void hasOneNodeAndNoEdges() {
        final var graph = givenOneNode();

        assertThat(graph.getNodes().toJavaMap()).containsExactly(n1, n1Val);
        assertThat(graph.getNodeValue(n1).getOrNull()).isEqualTo(n1Val);

        assertThat(graph.getEdges()).isEmpty();
    }

    AdjacencyGraph<String, String> givenOneNodeAndOneCycle() {
        return givenOneNode().putEdge(EdgeId.of(n1, n1), e1Val);
    }

    @Test
    void hasOneNodeAndOneCycle() {
        final var graph = givenOneNodeAndOneCycle();

        assertThat(graph.getNodes().toJavaMap()).containsExactly(n1, n1Val);
        assertThat(graph.getNodeValue(n1).getOrNull()).isEqualTo(n1Val);

        EdgeId e1 = EdgeId.of(n1, n1);
        assertThat(graph.getEdges().toJavaMap()).containsExactly(e1, e1Val);
        assertThat(graph.getEdgeValue(e1).getOrNull()).isEqualTo(e1Val);
    }
}
