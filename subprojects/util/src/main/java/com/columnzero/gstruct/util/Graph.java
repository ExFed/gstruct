package com.columnzero.gstruct.util;

import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.Value;

/**
 * A simple, immutable, directed graph.
 */
public interface Graph<N, E> {
    /**
     * Adds a new node or overwrites an existing node.
     *
     * @param id    Key that identifies the node.
     * @param value Value associated with the node.
     * @return A new immutable graph.
     */
    Graph<N, E> putNode(NodeId id, N value);

    /**
     * Attempts to get the value of a node.
     *
     * @param id Key that identifies the node.
     * @return {@link Option.Some} value if the node exists, otherwise
     *         {@link Option.None}.
     */
    Option<N> getNodeValue(NodeId id);

    /**
     * Gets nodes from which this node is a neighbor.
     *
     * @param id Key that identifies the node.
     * @return A set of node identifiers.
     */
    Set<NodeId> getNeighborsOut(NodeId id);

    /**
     * Remove a node from the graph if it exists.
     *
     * @param id Key that identifies the node.
     * @return A new immutable graph.
     */
    Graph<N, E> removeNode(NodeId id);

    /**
     * If both edges exist, adds a new edge or overwrites an existing edge.
     *
     * @param id    Key that identifies the edge.
     * @param value Value associated with the edge.
     * @return A new immutable graph.
     */
    Graph<N, E> putEdge(EdgeId id, E value);

    /**
     * Attempts to get the value of an edge.
     *
     * @param id Key that identifies the edge.
     * @return {@link Option.Some} value if the edge exists, otherwise
     *         {@link Option.None}.
     */
    Option<E> getEdgeValue(EdgeId id);

    /**
     * Remove a edge from the graph if it exists.
     *
     * @param id Key that identifies the edge.
     * @return A new immutable graph.
     */
    Graph<N, E> removeEdge(EdgeId id);

    @Value(staticConstructor = "of")
    static class NodeId implements Comparable<NodeId> {
        public static NodeId of(String first, String... tail) {
            return of(FQName.of(first, tail));
        }

        @NonNull
        FQName value;

        @Override
        public int compareTo(NodeId o) {
            return this.getValue().compareTo(o.getValue());
        }
    }

    @Value(staticConstructor = "of")
    static class EdgeId implements Comparable<EdgeId> {
        @NonNull
        NodeId from;
        @NonNull
        NodeId to;

        @Override
        public int compareTo(EdgeId o) {
            return compareToForward(o);
        }

        private int compareToForward(EdgeId o) {
            final var fromCmp = this.getFrom().compareTo(o.getFrom());
            return 0 != fromCmp ? fromCmp : this.getTo().compareTo(o.getTo());
        }
    }
}
