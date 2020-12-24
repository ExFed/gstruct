package com.columnzero.gstruct.util;

import com.columnzero.gstruct.util.Graph.Edge;
import com.columnzero.gstruct.util.Graph.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

class GraphTest {

    private final String nodeVal0 = "foo";
    private final String edgeVal0 = "bar";
    private Graph<String, String> graph;
    private final FQName idAbc = FQName.of("c", "a", "b");

    @BeforeEach
    void setUp() {
        graph = new Graph<>();
    }

    @Test
    void isEmpty() {
        assertThat(graph.getNodes()).isEmpty();
        assertThat(graph.getEdges()).isEmpty();
    }

    private Node<String, String> givenOneNode() {
        final Node<String, String> node = graph.addNode(idAbc);
        node.setValue(nodeVal0);
        return node;
    }

    @Test
    void hasOneNode() {
        final Node<String, String> node = givenOneNode();

        assertThat(node.getId()).isEqualTo(idAbc);
        assertThat(node.getValue()).isEqualTo(nodeVal0);
        assertThat(node.getEdgesOut()).isEmpty();

        assertThat(graph.findNode(idAbc).orElseThrow()).isSameInstanceAs(node);
        assertThat(graph.findEdge(idAbc, idAbc).isPresent()).isFalse();
        assertThat(graph.getOutgoingNeighbors(idAbc)).isEmpty();
        assertThat(graph.areAdjacent(idAbc, idAbc)).isFalse();
        assertThat(graph.getNodes()).containsExactly(node).inOrder();
        assertThat(graph.getEdges()).isEmpty();
    }

    private Edge<String, String> givenOneNodeWithCyclicalEdge() {
        return givenOneNodeWithCyclicalEdge(givenOneNode());
    }

    private Edge<String, String> givenOneNodeWithCyclicalEdge(Node<String, String> node) {
        final Edge<String, String> edge = graph.addEdge(node.getId(), node.getId()).orElseThrow();
        edge.setValue(edgeVal0);
        return edge;
    }

    @Test
    void hasOneNodeWithCyclicalEdge() {
        final Node<String, String> node = givenOneNode();
        final Edge<String, String> edge = givenOneNodeWithCyclicalEdge(node);


        assertThat(node.getGraph()).isSameInstanceAs(graph);
        assertThat(node.hasEdge(edge)).isTrue();
        assertThat(node.getEdgesOut()).containsExactlyEntriesIn(Map.of(idAbc, edge)).inOrder();

        assertThat(edge.getGraph()).isSameInstanceAs(graph);
        assertThat(edge.getValue()).isEqualTo(edgeVal0);


        assertThat(graph.findNode(idAbc).orElseThrow()).isSameInstanceAs(node);
        assertThat(graph.findEdge(idAbc, idAbc).orElseThrow()).isSameInstanceAs(edge);
        assertThat(graph.getOutgoingNeighbors(idAbc)).containsExactly(idAbc);
        assertThat(graph.areAdjacent(idAbc, idAbc)).isTrue();
        assertThat(graph.getNodes()).containsExactly(node).inOrder();
        assertThat(graph.getEdges()).containsExactly(edge).inOrder();

        assertThat(edge.isIncidentTo(edge)).isTrue();
    }

    @Test
    void addAndRemoveNode() {
        final Node<String, String> node = givenOneNode();

        node.remove();

        isEmpty();
    }

    @Test
    void addAndRemoveEdge() {
        final Edge<String, String> edge = givenOneNodeWithCyclicalEdge();

        graph.removeEdge(idAbc, idAbc);

        assertThat(graph.getNodes()).containsExactly(edge.getFrom());
        assertThat(graph.getEdges()).isEmpty();
    }
}
