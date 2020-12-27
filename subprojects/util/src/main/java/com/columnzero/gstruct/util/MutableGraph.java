package com.columnzero.gstruct.util;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple adjacency graph.
 */
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MutableGraph<N, E> {

    @EqualsAndHashCode.Include
    private final Map<FQName, Node<N, E>> nodes = new LinkedHashMap<>();

    public Node<N, E> addNode(FQName id) {
        return nodes.computeIfAbsent(id, fqName -> new Node<>(this, fqName));
    }

    public Optional<Node<N, E>> findNode(FQName id) {
        return Optional.ofNullable(nodes.get(id));
    }

    public Optional<Edge<N, E>> findEdge(FQName fromId, FQName toId) {
        return findNode(fromId).flatMap(from -> from.findEdge(toId));
    }

    public Set<FQName> getOutgoingNeighbors(FQName nodeId) {
        return findNode(nodeId).map(Node::getOutgoingNeighbors)
                               .stream()
                               .flatMap(Collection::stream)
                               .map(Node::getId)
                               .collect(Collectors.toUnmodifiableSet());
    }

    public boolean areAdjacent(FQName fromId, FQName toId) {
        return findNode(fromId).flatMap(from -> findNode(toId).map(from::isAdjacentTo))
                               .orElse(false);
    }

    public void removeNode(FQName id) {
        nodes.remove(id);
    }

    public Optional<Edge<N, E>> addEdge(FQName fromId, FQName toId) {
        return findNode(fromId).flatMap(from -> findNode(toId).map(from::addEdge));
    }

    public void removeEdge(FQName fromId, FQName toId) {
        findEdge(fromId, toId).ifPresent(Edge::remove);
    }

    @ToString.Include(name = "nodes")
    public Collection<Node<N, E>> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    @ToString.Include(name = "edges")
    public Collection<Edge<N, E>> getEdges() {
        return nodes.values()
                    .stream()
                    .map(Node::getEdgesOut)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toUnmodifiableList());
    }

    @Data
    @ToString(onlyExplicitlyIncluded = true)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Node<N, E> {
        private final @NonNull MutableGraph<N, E> graph;

        @ToString.Include
        @EqualsAndHashCode.Include
        private final @NonNull FQName id;

        @ToString.Include
        private N value;

        private final Map<FQName, Edge<N, E>> edgesOut = new LinkedHashMap<>();

        public boolean isAdjacentTo(Node<N, E> other) {
            return edgesOut.containsKey(other.getId());
        }

        public Set<Node<N, E>> getOutgoingNeighbors() {
            return edgesOut.values()
                           .stream()
                           .map(Edge::getTo)
                           .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        public void remove() {
            graph.removeNode(this.getId());
        }

        public Edge<N, E> addEdge(Node<N, E> target) {
            return edgesOut.computeIfAbsent(target.getId(),
                                            id -> new Edge<>(graph, this, target));
        }

        public boolean hasEdge(Edge<N, E> edge) {
            return edgesOut.containsKey(edge.getTo().getId());
        }

        private Optional<Edge<N, E>> findEdge(FQName toNodeId) {
            return Optional.ofNullable(edgesOut.get(toNodeId));
        }
    }

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString(onlyExplicitlyIncluded = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Edge<N, E> {
        private final @NonNull MutableGraph<N, E> graph;

        @EqualsAndHashCode.Include
        private final @NonNull Node<N, E> from;

        @EqualsAndHashCode.Include
        private final @NonNull Node<N, E> to;

        @ToString.Include(rank = -1)
        private E value;

        public boolean isIncidentTo(Edge<N, E> edge) {
            return to.hasEdge(edge);
        }

        public void remove() {
            from.edgesOut.remove(to.getId());
        }

        @ToString.Include(name = "from")
        private FQName getFromId() {
            return from.getId();
        }

        @ToString.Include(name = "to")
        private FQName getToId() {
            return to.getId();
        }
    }
}
