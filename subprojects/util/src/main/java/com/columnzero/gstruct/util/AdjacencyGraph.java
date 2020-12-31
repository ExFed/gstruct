package com.columnzero.gstruct.util;

import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Multimap;
import io.vavr.collection.Set;
import io.vavr.collection.TreeMap;
import io.vavr.collection.TreeMultimap;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdjacencyGraph<N, E> implements Graph<N, E> {
    public static <N, E> AdjacencyGraph<N, E> empty() {
        return new AdjacencyGraph<>(TreeMap.empty(),
                                    TreeMap.empty(),
                                    TreeMultimap.withSet().empty());
    }

    @NonNull Map<NodeId, N> nodes;
    @NonNull Map<EdgeId, E> edges;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Getter(AccessLevel.PRIVATE)
    @NonNull Multimap<NodeId, NodeId> adjacency;

    @Override
    public AdjacencyGraph<N, E> putNode(NodeId id, N value) {
        final var newNodes = nodes.put(id, value);
        return new AdjacencyGraph<>(newNodes, edges, adjacency);
    }

    @Override
    public Option<N> getNodeValue(NodeId id) {
        return nodes.get(id);
    }

    @Override
    public Set<NodeId> getNeighborsOut(NodeId id) {
        return adjacency.get(id).getOrElse(LinkedHashSet::empty).toLinkedSet();
    }

    @Override
    public AdjacencyGraph<N, E> removeNode(NodeId id) {
        final var newNodes = nodes.remove(id);
        final var newAdjacency = adjacency.remove(id);
        return new AdjacencyGraph<>(newNodes, edges, newAdjacency);
    }

    @Override
    public AdjacencyGraph<N, E> putEdge(EdgeId id, E value) {
        final var newEdges = edges.put(id, value);
        final var newAdjacency = adjacency.put(id.getFrom(), id.getTo());
        return new AdjacencyGraph<>(nodes, newEdges, newAdjacency);
    }

    @Override
    public Option<E> getEdgeValue(EdgeId id) {
        return edges.get(id);
    }

    @Override
    public AdjacencyGraph<N, E> removeEdge(EdgeId id) {
        final var newEdges = edges.remove(id);
        final var newAdjacency = adjacency.remove(id.getFrom());
        return new AdjacencyGraph<>(nodes, newEdges, newAdjacency);
    }
}
