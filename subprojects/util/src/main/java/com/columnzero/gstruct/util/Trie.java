package com.columnzero.gstruct.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A trie/prefix-tree that keys on generic token strings, not just character strings.
 *
 * @param <T> type of tokens that form paths in the tree
 * @param <V> type of mapped values
 */
public class Trie<T, V> {

    private final Node root = new Node();

    private int size = 0;

    public int size() {
        return size;
    }

    public boolean containsKey(Object key) {
        final Node node = findNode(key);
        return node != null && node.hasValue();
    }

    public V get(Object key) {
        final Node node = findNode(key);
        return node == null ? null : node.getValue();
    }

    public V put(Path<T> path, V value) {
        final Node node = makeNode(path);

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
        final Node node = findNode(key);

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
    private Node findNode(Object key) {
        if (!(key instanceof Path)) {
            return null;
        }

        Node node = root;
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
    private Node makeNode(Path<T> path) {
        Node node = root;
        for (T pathElem : path) {
            node = node.putChildIfAbsent(pathElem);
        }
        return node;
    }

    /**
     * Recursively accumulates depth-first for nodes that have value
     */
    private Set<? extends Entry<Path<T>, V>> entrySetRecurse(Set<NodeEntry> entries,
                                                             Path<T> path,
                                                             Node node) {
        if (node.hasValue()) {
            entries.add(new NodeEntry(path, node));
        }

        for (Entry<T, Node> childEntry : node.getChildEntries()) {
            final Path<T> childPath = path.child(childEntry.getKey());
            entrySetRecurse(entries, childPath, childEntry.getValue());
        }

        return entries;
    }

    /**
     * A node within the tree.
     */
    private final class Node implements PrefixNode<T, V> {

        private final Map<T, Node> children = new LinkedHashMap<>();

        private boolean present = false;
        private V value = null;

        @Override
        public boolean hasValue() {
            return present;
        }

        @Override
        public V getValue() {
            return hasValue() ? value : null;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
            this.present = true;
        }

        @Override
        public void removeValue() {
            this.value = null;
            this.present = false;
        }

        @Override
        public Node putChildIfAbsent(T token) {
            return children.computeIfAbsent(token, k -> new Node());
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        public boolean hasChild(Object token) {
            return children.containsKey(token);
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        public Node getChild(Object token) {
            return children.get(token);
        }

        @SuppressWarnings("SuspiciousMethodCalls")
        @Override
        public PrefixNode<T, V> removeChild(Object token) {
            return children.remove(token);
        }

        /**
         * The raw entry set of children.
         */
        private Set<Entry<T, Node>> getChildEntries() {
            return children.entrySet();
        }
    }

    /**
     * An entry within the entry set.
     */
    private final class NodeEntry implements Entry<Path<T>, V> {

        private final Path<T> path;
        private final Node node;

        private NodeEntry(Path<T> path, Node node) {
            this.path = requireNonNull(path);
            this.node = requireNonNull(node);
        }

        @Override
        public Path<T> getKey() {
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
