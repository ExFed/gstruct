package com.columnzero.gstruct.util.function;

/**
 * A bunch of no-operation functions.
 */
public class NoOp {

    private NoOp() {
        throw new AssertionError("util class");
    }

    public static void noopVoid(Object... o) {
    }
}
