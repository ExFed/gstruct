package com.columnzero.gstruct.util;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.requireNonNull;

/**
 * A trie/prefix-tree.
 *
 * @param <T> type of tokens that form paths in the tree
 * @param <V> type of mapped values
 */
public class Trie<T, V> {

    private final Node<T, V> root = new Node<>();

    private int size = 0;

    public int size() {
        return size;
    }

    public boolean containsKey(Object key) {
        final Node<T, V> node = findNode(key);
        return node != null && node.hasValue();
    }

    public V get(Object key) {
        final Node<T, V> node = findNode(key);
        return node == null ? null : node.getValue();
    }

    public V put(Path<T> path, V value) {
        final Node<T, V> node = makeNode(path);

        if (!node.hasValue()) {
            size++;
        }

        final V oldValue = node.getValue();
        node.setValue(value);
        return oldValue;
    }

    public void putAll(Map<? extends Path<T>, ? extends V> map) {
        for (Entry<? extends Path<T>, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    public V remove(Object key) {
        final Node<T, V> node = findNode(key);

        if (node == null) {
            return null;
        }

        if (node.hasValue()) {
            size--;
        }

        final V oldValue = node.getValue();
        node.removeValue();
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    public Set<Entry<Path<T>, V>> entrySet() {
        return (Set<Entry<Path<T>, V>>) entrySetRecurse(new LinkedHashSet<>(),
                                                        Path.getRoot(),
                                                        root);
    }

    @Override
    public String toString() {
        return entrySet().toString();
    }

    /**
     * Iteratively searches for the node at the given path, returns null if none found.
     */
    private Node<T, V> findNode(Object key) {
        if (!(key instanceof Path)) {
            return null;
        }

        Node<T, V> node = root;
        for (Object pathElem : (Path<?>) key) {
            if (!node.hasChild(pathElem)) {
                return null;
            }
            node = node.getChild(pathElem);
        }
        return node;
    }

    /**
     * Iteratively makes a new node at the given path if it doesn't already exist.
     */
    private Node<T, V> makeNode(Path<T> path) {
        Node<T, V> node = root;
        for (T pathElem : path) {
            node = node.makeChild(pathElem);
        }
        return node;
    }

    /**
     * Recursively accumulates depth-first for nodes that have value
     */
    private static <T, V> Set<? extends Entry<Path<T>, V>> entrySetRecurse(Set<NodeEntry<T, V>> entries,
                                                                           Path<T> path,
                                                                           Node<T, V> node) {
        if (node.hasValue()) {
            entries.add(new NodeEntry<>(path, node));
        }

        for (Entry<T, Node<T, V>> childEntry : node.getChildEntries()) {
            final Path<T> childPath = path.child(childEntry.getKey());
            entrySetRecurse(entries, childPath, childEntry.getValue());
        }

        return entries;
    }

    /**
     * A node within the tree.
     */
    private static final class Node<E, V> {

        final Map<E, Node<E, V>> children = new LinkedHashMap<>();

        boolean present = false;
        V value = null;

        boolean hasValue() {
            return present;
        }

        V getValue() {
            return hasValue() ? value : null;
        }

        void setValue(V value) {
            this.value = value;
            this.present = true;
        }

        void removeValue() {
            this.value = null;
            this.present = false;
        }

        /**
         * Creates a child node if it doesn't already exist.
         */
        Node<E, V> makeChild(E pathElem) {
            return children.computeIfAbsent(pathElem, k -> new Node<>());
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        boolean hasChild(Object key) {
            return children.containsKey(key);
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        Node<E, V> getChild(Object key) {
            return children.get(key);
        }

        Set<Entry<E, Node<E, V>>> getChildEntries() {
            return children.entrySet();
        }
    }

    /**
     * An entry within the entry set.
     */
    private static final class NodeEntry<E, V> implements Entry<Path<E>, V> {

        private final Path<E> path;
        private final Node<E, V> node;

        private NodeEntry(Path<E> path, Node<E, V> node) {
            this.path = requireNonNull(path);
            this.node = requireNonNull(node);
        }

        @Override
        public Path<E> getKey() {
            return path;
        }

        @Override
        public V getValue() {
            return node.getValue();
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> other = (Map.Entry<?, ?>) o;
                return getKey().equals(other.getKey()) &&
                        getValue().equals(other.getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, node);
        }

        @Override
        public String toString() {
            return path + "=" + node.getValue();
        }
    }
}
