package com.columnzero.gstruct.model;

final class Util {
    /**
     * Lexicographically compares two iterables. See {@link java.util.Comparator}
     *
     * @param i1  First sequence.
     * @param i2  Second sequence.
     * @param <E> Type of elements contained within the iterables.
     *
     * @return -1 if {@code i1} is greater, +1 if {@code i2} is greater, or 0 if they are equal
     */
    public static <E extends Comparable<E>> int lexicalCompare(Iterable<E> i1, Iterable<E> i2) {
        var it1 = i1.iterator();
        var it2 = i2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            int cmp = it1.next().compareTo(it2.next());
            if (cmp != 0) {
                return cmp;
            }
        }

        if (it2.hasNext()) {
            return -1; // i1 is shorter than i2
        } else if (it1.hasNext()) {
            return 1; // i1 is longer than i2
        } else {
            return 0; // i1 is equal to i2
        }
    }
}
