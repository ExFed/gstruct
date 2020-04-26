package com.columnzero.gstruct.util.function;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * The result of a function call that may throw an exception.
 *
 * @param <V> Type of success value.
 */
public class CallResult<V> {

    private static final CallResult<Object> EMPTY_SUCCESS = success(null);

    /**
     * Constructs the result of a successful call.
     *
     * @param value Value of the result.
     * @param <V>   Type of success value.
     *
     * @return A new success result.
     */
    public static <V> CallResult<V> success(V value) {
        return new CallResult<>(value);
    }

    /**
     * Constructs the result of a failed call.
     *
     * @param error Error thrown by the call.
     * @param <V>   Type of success value.
     *
     * @return A new failure result.
     */
    public static <V> CallResult<V> failure(Exception error) {
        return new CallResult<>(error);
    }

    /**
     * Calls the given {@link Callable} and returns the result.
     *
     * @param callable Callable to call.
     * @param <V>      Type of success value.
     *
     * @return A new result.
     */
    public static <V> CallResult<V> of(Callable<V> callable) {
        Objects.requireNonNull(callable);

        return EMPTY_SUCCESS.map(arg -> callable.call());
    }

    private final V value;
    private final Exception error; // hasValue == TRUE iff error == null

    private CallResult(V value) {
        this.value = value;
        this.error = null;
    }

    private CallResult(Exception error) {
        this.value = null;
        this.error = Objects.requireNonNull(error);
    }

    /**
     * Gets whether this result was successful. If false, then {@link #get()} will throw an
     * exception.
     *
     * @return True if the call resulted in success, otherwise false.
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * Gets the value of the result or throws the exception if it exists.
     *
     * @return Value of the result if the call was successful.
     *
     * @throws Exception If the result of the call was not successful.
     */
    public V get() throws Exception {
        if (error != null) {
            throw error;
        }

        return value;
    }

    /**
     * If this is a successful result, returns the result of applying the given function to the
     * value of this result, otherwise returns the current failure result.
     *
     * @param mapper Function to apply.
     * @param <R>    Type of success value.
     *
     * @return A new result.
     */
    @SuppressWarnings("unchecked")
    public <R> CallResult<R> map(Mapper<V, R, Exception> mapper) {
        if (!isSuccess()) {
            return (CallResult<R>) this;
        }

        try {
            return success(mapper.apply(value));
        } catch (Exception e) {
            return failure(e);
        }
    }
}
