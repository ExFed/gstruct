package com.columnzero.gstruct.util.function;

/**
 * Function that maps from one type to another. May throw an exception.
 *
 * @param <T> Type of the argument value.
 * @param <R> Type of the return value.
 * @param <E> Type of the thrown exception.
 */
@FunctionalInterface
public interface Mapper<T, R, E extends Exception> {

    /**
     * Applies the function to the given argument.
     *
     * @param t The argument value
     * @return The return value
     * @throws E If there is an error.
     */
    R apply(T t) throws E;
}
