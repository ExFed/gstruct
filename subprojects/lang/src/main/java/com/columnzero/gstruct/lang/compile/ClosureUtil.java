package com.columnzero.gstruct.lang.compile;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ClosureUtil {

    private ClosureUtil() {
        throw new AssertionError("utility");
    }

    public static <T, R> Closure<R> asClosure(Object owner, Function<T, R> fn) {
        return new Closure<R>(owner) {
            @SuppressWarnings("unused")
            public R doCall(T arg) {
                return fn.apply(arg);
            }
        };
    }

    public static <R> Closure<R> asClosure(Object owner, Supplier<R> fn) {
        return new Closure<R>(owner) {
            @SuppressWarnings("unused")
            public R doCall() {
                return fn.get();
            }
        };
    }

    public static <T> Closure<Void> asClosure(Object owner, Consumer<T> fn) {
        return new Closure<Void>(owner) {
            @SuppressWarnings("unused")
            public void doCall(T arg) {
                fn.accept(arg);
            }
        };
    }

    public static <T, R> Closure<R> asListClosure(Object owner, Function<List<T>, R> function) {
        return new Closure<R>(owner) {
            @SuppressWarnings({"unused", "unchecked"})
            public final R doCall(Object... args) {
                List<T> argList = Arrays.stream(args).map(a -> (T) a).collect(Collectors.toList());
                return function.apply(argList);
            }
        };
    }
}
