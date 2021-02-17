package com.columnzero.gstruct.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class UtilTest {

    private static Stream<Arguments> lexicalCompareSource() {
        return Stream.of(
                arguments(0, asList("a", "a"), asList("a", "a")),
                arguments(-1, asList("a", "a"), asList("a", "b")),
                arguments(+1, asList("a", "b"), asList("a", "a")),
                arguments(-1, asList("a", "a"), asList("a", "a", "a")),
                arguments(+1, asList("a", "a", "a"), asList("a", "a")));
    }

    @ParameterizedTest
    @MethodSource("lexicalCompareSource")
    void lexicalCompare(int expect, Iterable<String> it1, Iterable<String> it2) {
        int actual = Util.lexicalCompare(it1, it2);
        assertThat(actual).isEqualTo(expect);
    }
}
