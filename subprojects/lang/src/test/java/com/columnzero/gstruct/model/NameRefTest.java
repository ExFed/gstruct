package com.columnzero.gstruct.model;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class NameRefTest {

    static final Ref<String> BAR = Ref.constRef("bar");
    static final Ref<String> BAZ = Ref.constRef("baz");

    static final NameRef<String> FOO_BAR = NameRef.of(BAR).named("foo");
    static final NameRef<String> FOO_BAZ = NameRef.of(BAZ).named("foo");
    static final NameRef<String> BIZ_BAZ = NameRef.of(BAZ).named("biz");
    static final String VALUE = "non ref";

    /**
     * Defined as:
     * <pre>
     *           :bar    :baz   foo:bar foo:baz biz:baz value
     *     :bar   ==      !=      !=      !=      !=      !=
     *     :baz   !=      ==      !=      !=      !=      !=
     *  foo:bar   !=      !=      ==      ==      !=      !=
     *  foo:baz   !=      !=      ==      ==      !=      !=
     *  biz:baz   !=      !=      !=      !=      ==      !=
     *    value   !=      !=      !=      !=      !=      ==
     * </pre>
     */
    static final boolean[][] EQUALITY_MATRIX = {
            {true, false, false, false, false, false,},
            {false, true, false, false, false, false,},
            {false, false, true, true, false, false,},
            {false, false, true, true, false, false,},
            {false, false, false, false, true, false,},
            {false, false, false, false, false, true,}
    };

    /**
     * Defined as: {@code [:bar, :baz, foo:bar, foo:baz, biz:baz, value]}
     */
    static final Object[] VECTOR = {
            BAR,
            BAZ,
            FOO_BAR,
            FOO_BAZ,
            BIZ_BAZ,
            VALUE
    };

    static Stream<Tuple3<Object, Object, Boolean>> getEqualityEdges() {
        Stream.Builder<Tuple3<Object, Object, Boolean>> triples = Stream.builder();
        for (int rowIdx = 0; rowIdx < EQUALITY_MATRIX.length; rowIdx++) {
            boolean[] equalityRow = EQUALITY_MATRIX[rowIdx];
            for (int colIdx = 0; colIdx < equalityRow.length; colIdx++) {
                var isEqual = equalityRow[colIdx];
                var row = VECTOR[rowIdx];
                var col = VECTOR[colIdx];
                triples.add(Tuple.of(row, col, isEqual));
            }
        }
        return triples.build();
    }

    private static Stream<Arguments> equalRefsSource() {
        return getEqualityEdges().filter(Tuple3::_3)
                                 .map(triple -> arguments(triple._1, triple._2));
    }

    private static Stream<Arguments> nonEqualRefsSource() {
        return getEqualityEdges().filter(not(Tuple3::_3))
                                 .map(triple -> arguments(triple._1, triple._2));
    }

    @ParameterizedTest
    @MethodSource("equalRefsSource")
    void equalRefsAreEqual(Object row, Object col) {
        assertThat(row.equals(col)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("nonEqualRefsSource")
    void nonEqualRefsAreNotEqual(Object row, Object col) {
        assertThat(row.equals(col)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("equalRefsSource")
    void equalRefsHaveSameHashCodes(Object row, Object col) {
        assertThat(row.hashCode()).isEqualTo(col.hashCode());
    }

    @ParameterizedTest
    @MethodSource("nonEqualRefsSource")
    void nonEqualRefsHaveDifferentHashCodes(Object row, Object col) {
        assertThat(row.hashCode()).isNotEqualTo(col.hashCode());
    }

    @Test
    void getKey() {
        assertThat(FOO_BAR.getKey()).isEqualTo(FOO_BAR.getName());
    }

    @Test
    void getValue() {
        assertThat(FOO_BAR.getValue()).isEqualTo(FOO_BAR.getRefGetter().apply(FOO_BAR.getName()));
    }

    @Test
    void setValue() {
        assertThrows(UnsupportedOperationException.class, () -> FOO_BAR.setValue(BAZ));
    }
}
