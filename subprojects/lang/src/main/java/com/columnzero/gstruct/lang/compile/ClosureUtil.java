package com.columnzero.gstruct.lang.compile;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FirstParam;

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
            @SuppressWarnings("unused")
            public final R doCall(Object... args) {
                return function.apply(asArgsList(args));
            }
        };
    }

    public static <T> Closure<Void> asListClosure(Object owner, Consumer<List<T>> fn) {
        return new Closure<Void>(owner) {
            @SuppressWarnings("unused")
            public final void doCall(Object... args) {
                fn.accept(asArgsList(args));
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> asArgsList(Object... args) {
        return Arrays.stream(args).map(a -> (T) a).collect(Collectors.toList());
    }

    public static <T, U> void invokeWith(U self, Closure<T> closure) {
        invokeWith(closure.getOwner(), self, closure);
    }

    public static <T, U> void invokeWith(
            Object owner,
            @DelegatesTo.Target("delegate") U self,
            @DelegatesTo(target = "delegate", strategy = Closure.DELEGATE_ONLY)
            @ClosureParams(FirstParam.class) Closure<T> closure) {

        final Closure<T> clonedClosure = closure.rehydrate(self, owner, self);
        clonedClosure.setResolveStrategy(Closure.DELEGATE_ONLY);
        clonedClosure.call(self);
    }
}
