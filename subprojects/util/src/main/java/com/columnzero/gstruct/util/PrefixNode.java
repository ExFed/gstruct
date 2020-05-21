package com.columnzero.gstruct.util;

/**
 * A node in a generic, tokenized prefix search tree.
 * <p>
 * Note that this is different from a typical prefix tree, as tokens within the tree may be
 * arbitrary objects, not just characters. Child nodes are uniquely keyed by their associated token.
 * That means tokens will have to satisfy conditions to determine uniqueness, depending upon
 * specific implementations.
 *
 * @param <T> Type of tokens associated with children.
 * @param <V> Type of values associated with nodes.
 */
public interface PrefixNode<T, V> {

    /**
     * Checks if this node has a value associated with it.
     *
     * @return True if there is a value at this node.
     */
    boolean hasValue();

    /**
     * Gets the value associated with this node.
     *
     * @return The node's value, or {@code null} if there is none.
     */
    V getValue();

    /**
     * Sets the value associated with this node. Subsequent calls to {@link #hasValue()} should
     * return {@code true}, regardless of value.
     *
     * @param value Value to set.
     */
    void setValue(V value);

    /**
     * Removes any value associated with this node. If no value exists, must be idempotent.
     * Subsequent calls to {@link #hasValue()} should return {@code false}.
     */
    void removeValue();

    /**
     * Creates a child associated with the given token if it doesn't already exist.
     *
     * @param token Key that maps to the child.
     *
     * @return The child associated with the token.
     */
    PrefixNode<T, V> putChildIfAbsent(T token);

    /**
     * Checks if this node contains a child associated with the given token.
     *
     * @param token Key associated with the child to test for presence.
     *
     * @return {@code true} if and only if this node contains an associated child.
     */
    boolean hasChild(Object token);

    /**
     * Gets the child associated with the given token.
     *
     * @param token Token associated with the child to get.
     *
     * @return The child associated with the token, or {@code null} if there is none.
     */
    PrefixNode<T, V> getChild(Object token);

    /**
     * Removes the child associated with the given token if it exists.
     *
     * @param token Token associated with the child to remove.
     *
     * @return The child that was removed, or {@code null} if there was none.
     */
    PrefixNode<T, V> removeChild(Object token);
}
