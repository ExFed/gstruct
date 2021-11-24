package com.columnzero.gstruct.util;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
@EqualsAndHashCode
public final class Trie<T, V> {

    private int size = 0;

    private final @NonNull Node root = new Node();

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

    public PrefixNode<T, V> getNode() {
        return root;
    }

    public Optional<PrefixNode<T, V>> getNode(Object key) {
        return Optional.ofNullable(findNode(key));
    }

    public V put(Path<? extends T> key, V value) {
        final Node node = makeNode(key);

        if (!node.hasValue()) {
            size++;
        }

        final V oldValue = node.getValue();
        node.setValue(value);
        return oldValue;
    }

    public void putAll(Map<? extends Path<T>, ? extends V> map) {
        putAllEntries(map.entrySet());
    }

    public void putAll(Trie<? extends T, ? extends V> trie) {
        putAllEntries(trie.entrySet());
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

    public Set<Entry<Path<T>, V>> entrySet() {
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
        if (!(key instanceof Path)) {
            return null;
        }

        Node node = root;
        for (Object keyToken : (Path<?>) key) {
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
    private Node makeNode(Path<? extends T> key) {
        Node node = root;
        for (T keyToken : key) {
            node = node.putChildIfAbsent(keyToken);
        }
        return node;
    }

    /**
     * Recursively accumulates depth-first for nodes that have value.
     */
    private Set<Entry<Path<T>, V>> entrySetRecurse(NodeEntrySet entries,
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

    private void putAllEntries(Set<? extends Entry<? extends Path<? extends T>, ? extends V>> eSet) {

        for (Entry<? extends Path<? extends T>, ? extends V> e : eSet) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * A node within the tree.
     */
    @EqualsAndHashCode
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
        public boolean isLeaf() {
            return children.isEmpty();
        }

        @Override
        public Map<T, PrefixNode<T, V>> getChildren() {
            return Collections.unmodifiableMap(children);
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

        @Override
        public String toString() {
            return buildString(new StringBuilder(), 0).toString();
        }

        private StringBuilder buildString(StringBuilder sb, int level) {
            if (hasValue()) {
                sb.append('(').append(getValue()).append(") ");
            }
            var indent = " ".repeat(level * 2);
            if (children.isEmpty()) {
                return sb.append('\n');
            }
            sb.append("{\n");
            for (Entry<T, Node> e : children.entrySet()) {
                var innerLevel = level + 1;
                var innerIndent = " ".repeat(innerLevel * 2);
                sb.append(innerIndent)
                  .append(e.getKey())
                  .append(": ");
                e.getValue()
                 .buildString(sb, innerLevel);
            }
            return sb.append(indent)
                     .append("}\n");
        }
    }

    /**
     * A set of entries.
     */
    private final class NodeEntrySet {

        private final Set<Entry<Path<T>, V>> inner = new LinkedHashSet<>();
    }

    /**
     * An entry within the entry set.
     */
    @Value
    private class NodeEntry implements Entry<Path<T>, V> {

        @NonNull Path<T> key;
        @NonNull Node node;

        @Override
        public Path<T> getKey() {
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
            return Objects.hashCode(key) ^ Objects.hashCode(node.getValue());
        }

        @Override
        public String toString() {
            return key + "=" + node.getValue();
        }
    }
}
