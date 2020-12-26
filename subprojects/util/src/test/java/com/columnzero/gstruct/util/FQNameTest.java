package com.columnzero.gstruct.util;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FQNameTest {

    @Test
    void compareTo() {
        final var array = new FQName[] {
            FQName.of("a", "aa", "aaa"),
            FQName.of("a", "a", "a"),
            FQName.of("a"),
            FQName.of("b"),
            FQName.of("c", "b", "a"),
            FQName.of("a", "b", "c"),
            FQName.of("a", "aa"),
        };

        final var expect = new FQName[] {
            FQName.of("a"),
            FQName.of("a", "a", "a"),
            FQName.of("a", "aa"),
            FQName.of("a", "aa", "aaa"),
            FQName.of("a", "b", "c"),
            FQName.of("b"),
            FQName.of("c", "b", "a"),
        };

        Arrays.sort(array);

        assertThat(array).isEqualTo(expect);
    }
}
