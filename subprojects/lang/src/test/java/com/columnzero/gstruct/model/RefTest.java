package com.columnzero.gstruct.model;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.columnzero.gstruct.model.Extern.extern;
import static com.google.common.truth.Truth.assertThat;
import static java.util.function.Predicate.not;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RefTest {

//    static final Ref<Type> BAR = () -> extern("bar");
//    static final Ref<Type> BAZ = () -> extern("baz");
//
//    static final Ref<Type> FOO_BAR_LAZY = Ref.lazy("foo", BAR);
//    static final Ref<Type> FOO_BAZ_LAZY = Ref.lazy("foo", BAZ);
//    static final Ref<Type> BIZ_BAZ_LAZY = Ref.lazy("biz", BAZ);
//    static final Ref<Type> FOO_BAR_EAGER = ((NameRef.Supplied) FOO_BAR_LAZY).asEager();
//    static final Ref<Type> FOO_BAZ_EAGER = ((NameRef.Supplied) FOO_BAZ_LAZY).asEager();
//    static final Ref<Type> BIZ_BAZ_EAGER = ((NameRef.Supplied) BIZ_BAZ_LAZY).asEager();
//    static final String NON_REF = "non ref";
//
//    /**
//     * Defined as:
//     * <pre>
//     *          foobar  foobaz  bizbaz  foobar' foobaz' bizbaz' nonref
//     *  foobar    ==      !=      !=      ==      !=      !=      !=
//     *  foobaz    !=      ==      !=      !=      ==      !=      !=
//     *  bizbaz    !=      !=      ==      !=      !=      ==      !=
//     *  foobar'   ==      !=      !=      ==      !=      !=      !=
//     *  foobaz'   !=      ==      !=      !=      ==      !=      !=
//     *  bizbaz'   !=      !=      ==      !=      !=      ==      !=
//     *  nonref    !=      !=      !=      !=      !=      !=      ==
//     * </pre>
//     */
//    static final boolean[][] EQUALITY_MATRIX = {
//            {true, false, false, true, false, false, false},
//            {false, true, false, false, true, false, false},
//            {false, false, true, false, false, true, false},
//            {true, false, false, true, false, false, false},
//            {false, true, false, false, true, false, false},
//            {false, false, true, false, false, true, false},
//            {false, false, false, false, false, false, true}
//
//    };
//
//    /**
//     * Defined as: {@code [foobar, foobaz, bizbaz, foobar', foobaz', bizbaz']}
//     */
//    static final Object[] REF_VECTOR = {
//            FOO_BAR_LAZY,
//            FOO_BAZ_LAZY,
//            BIZ_BAZ_LAZY,
//            FOO_BAR_EAGER,
//            FOO_BAZ_EAGER,
//            BIZ_BAZ_EAGER,
//            NON_REF
//    };
//
//    static Stream<Tuple3<Object, Object, Boolean>> getEqualityEdges() {
//        Stream.Builder<Tuple3<Object, Object, Boolean>> triples = Stream.builder();
//        for (int rowIdx = 0; rowIdx < EQUALITY_MATRIX.length; rowIdx++) {
//            boolean[] equalityRow = EQUALITY_MATRIX[rowIdx];
//            for (int colIdx = 0; colIdx < equalityRow.length; colIdx++) {
//                var isEqual = equalityRow[colIdx];
//                var a = REF_VECTOR[rowIdx];
//                var b = REF_VECTOR[colIdx];
//                triples.add(Tuple.of(a, b, isEqual));
//            }
//        }
//        return triples.build();
//    }
//
//    private static Stream<Arguments> equalRefsSource() {
//        return getEqualityEdges().filter(Tuple3::_3)
//                                 .map(triple -> arguments(triple._1, triple._2));
//    }
//
//    private static Stream<Arguments> nonEqualRefsSource() {
//        return getEqualityEdges().filter(not(Tuple3::_3))
//                                 .map(triple -> arguments(triple._1, triple._2));
//    }
//
//    @ParameterizedTest
//    @MethodSource("equalRefsSource")
//    void equalRefsAreEqual(Object a, Object b) {
//        assertThat(a.equals(b)).isTrue();
//    }
//
//    @ParameterizedTest
//    @MethodSource("nonEqualRefsSource")
//    void nonEqualRefsAreNotEqual(Object a, Object b) {
//        assertThat(a.equals(b)).isFalse();
//    }
//
//    @ParameterizedTest
//    @MethodSource("equalRefsSource")
//    void equalRefsHaveSameHashCodes(Object a, Object b) {
//        assertThat(a.hashCode()).isEqualTo(b.hashCode());
//    }
//
//    @ParameterizedTest
//    @MethodSource("nonEqualRefsSource")
//    void nonEqualRefsHaveDifferentHashCodes(Object a, Object b) {
//        assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
//    }
}
