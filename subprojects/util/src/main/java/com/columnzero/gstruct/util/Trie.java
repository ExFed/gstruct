package com.columnzero.gstruct.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * An associative prefix tree of generic token strings.
 * <p>
 * {@link Trie} is distinct from the typical trie in that it keys on token strings, not character
 * sequences. As this implementation is backed by {@link LinkedHashMap}, tokens should override
 * {@link Object#equals(Object)} and {@link Object#hashCode()}.
 *
 * @param <T> Type of tokens that form paths in the tree.
 * @param <V> Type of mapped values.
 *
 * @see LinkedHashMap
 * @see Object#hashCode()
 * @see Object#equals(Object)
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

    public V put(Iterable<? extends T> key, V value) {
        final Node node = makeNode(key);

        if (!node.hasValue()) {
            size++;
        }

        final V oldValue = node.getValue();
        node.setValue(value);
        return oldValue;
    }

    public void putAll(Map<? extends Iterable<T>, ? extends V> map) {
        putAllEntries(map.entrySet());
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

    public Set<Entry<Iterable<T>, V>> entrySet() {
        return entrySetRecurse(new NodeEntrySet(),
                               Path.getRoot(),
                               root);
    }

    @Override
    public String toString() {
        return entrySet().toString();
    }

    /**
     * Iteratively searches for the node at the given key, returns null if none found.
     */
    private Node findNode(Object key) {
        if (!(key instanceof Iterable)) {
            return null;
        }

        Node node = root;
        for (Object keyToken : (Iterable<?>) key) {
            if (!node.hasChild(keyToken)) {
                return null;
            }
            node = node.getChild(keyToken);
        }
        return node;
    }

    /**
     * Iteratively makes a new node at the given key if it doesn't already exist.
     */
    private Node makeNode(Iterable<? extends T> key) {
        Node node = root;
        for (T keyToken : key) {
            node = node.putChildIfAbsent(keyToken);
        }
        return node;
    }

    /**
     * Recursively accumulates depth-first for nodes that have value.
     */
    private Set<Entry<Iterable<T>, V>> entrySetRecurse(NodeEntrySet entries,
                                                       Path<T> key,
                                                       Node node) {
        if (node.hasValue()) {
            entries.inner.add(new NodeEntry(key, node));
        }

        for (Entry<T, Node> childEntry : node.getChildEntries()) {
            final Path<T> childPath = key.child(childEntry.getKey());
            entrySetRecurse(entries, childPath, childEntry.getValue());
        }

        return entries.inner;
    }

    private void putAllEntries(Set<? extends Entry<? extends Iterable<? extends T>, ? extends V>> eSet) {

        for (Entry<? extends Iterable<? extends T>, ? extends V> e : eSet) {
            put(e.getKey(), e.getValue());
        }
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
     * A set of entries.
     */
    private final class NodeEntrySet extends AbstractSet<Entry<Iterable<T>, V>> {
        private final Set<Entry<Iterable<T>, V>> inner = new LinkedHashSet<>();

        @Override
        public Iterator<Entry<Iterable<T>, V>> iterator() {
            return inner.iterator();
        }

        @Override
        public int size() {
            return inner.size();
        }
    }

    /**
     * An entry within the entry set.
     */
    private final class NodeEntry implements Entry<Iterable<T>, V> {

        private final Iterable<T> key;
        private final Node node;

        private NodeEntry(Iterable<T> key, Node node) {
            this.key = requireNonNull(key);
            this.node = requireNonNull(node);
        }

        @Override
        public Iterable<T> getKey() {
            return key;
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
            return Objects.hash(key, node);
        }

        @Override
        public String toString() {
            return key + "=" + node.getValue();
        }
    }
}
