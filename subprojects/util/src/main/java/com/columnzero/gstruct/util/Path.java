package com.columnzero.gstruct.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Path<T> implements Iterable<T> {

    private static final Path<Object> ROOT = new Path<>(new RootValue());

    /**
     * Gets the global root node.
     */
    @SuppressWarnings("unchecked")
    public static <T> Path<T> getRoot() {
        return (Path<T>) ROOT;
    }

    /**
     * Creates a path that forms part of the global tree.
     *
     * @param elements Elements of the path. The first element is attached to the global root, the
     *                 last element becomes a leaf.
     * @param <T>      Type of elements within the path.
     *
     * @return A new path.
     */
    @SafeVarargs
    public static <T> Path<T> of(T... elements) {
        return of(Arrays.asList(elements));
    }

    /**
     * Creates a path that forms part of the global tree.
     *
     * @param elements Elements of the path. The first element is attached to the global root, the
     *                 last element becomes a leaf.
     * @param <T>      Type of elements within the path.
     *
     * @return A new path.
     */
    public static <T> Path<T> of(Iterable<T> elements) {
        Path<T> result = getRoot();
        for (T element : elements) {
            result = result.child(element);
        }
        return result;
    }

    private final T value;
    private final Path<T> parent;
    private final int depth;

    private Path(T value) {
        this(value, null);
    }

    private Path(T value, Path<T> parent) {
        this.value = Objects.requireNonNull(value);
        this.parent = parent;
        this.depth = parent != null ? parent.depth + 1 : 0;
    }

    public T getValue() {
        return value;
    }

    public Path<T> getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * Creates a path whose parent is this path instance.
     *
     * @param value Value of the new path node.
     *
     * @return A new path.
     */
    public Path<T> child(T value) {
        return new Path<>(value, this);
    }

    public Path<T> child(Iterable<T> values) {
        return childIt(values.iterator());
    }

    private Path<T> childIt(Iterator<T> it) {
        return it.hasNext() ? this.child(it.next()).childIt(it) : this;
    }

    /**
     * Generates a list representation of this path. The first element is the base, the last element
     * is the leaf.
     *
     * @return A list of the path elements.
     */
    public List<T> asList() {
        return asListRecurse(new ArrayList<>(depth));
    }

    private List<T> asListRecurse(List<T> result) {
        // traverse the parent if it exists
        if (this.parent != null) {
            parent.asListRecurse(result);
        }

        // should not emit the global root node
        if (this != ROOT) {
            // post-order insertion...
            result.add(this.value);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Path<?> path = (Path<?>) o;
        return value.equals(path.value)
                && depth == path.depth
                && Objects.equals(parent, path.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, parent);
    }

    @Override
    public Iterator<T> iterator() {
        return asList().iterator();
    }

    private StringJoiner buildString(StringJoiner sj) {
        if (parent != null) {
            parent.buildString(sj);
        }
        return sj.add(value.toString());
    }

    public String toString(CharSequence separator) {
        return buildString(new StringJoiner(separator)).toString();
    }

    @Override
    public String toString() {
        return toString("/");
    }

    /**
     * The global root node value.
     */
    private static final class RootValue {
        @Override
        public String toString() {
            return "";
        }
    }
}
