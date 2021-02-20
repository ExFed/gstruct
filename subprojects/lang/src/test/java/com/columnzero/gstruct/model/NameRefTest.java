package com.columnzero.gstruct.model;

import com.columnzero.gstruct.util.TestUtil;
import com.columnzero.gstruct.util.TestUtil.EqualityEdge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.columnzero.gstruct.model.Extern.extern;
import static com.google.common.truth.Truth.assertThat;
import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class NameRefTest {

    static final NominalModel model;

    static final Ref<Type> BAR;
    static final Ref<Type> BAZ;

    static final NameRef FOO_BAR;
    static final NameRef BIZ_BAZ;
    static final NameRef QUX_BAZ;

    static {
        model = new NominalModel();
        BAR = Ref.constRef(extern("bar"));
        BAZ = Ref.constRef(extern("baz"));
        FOO_BAR = model.bind(BAR).to("foo");
        BIZ_BAZ = model.bind(BAZ).to("biz");
        QUX_BAZ = model.bind(BAZ).to("qux");
    }

    static final String VALUE = "non ref";

    /**
     * Defined as:
     * <pre>
     *           :bar    :baz   foo:bar biz:baz qux:baz value
     *     :bar   ==      !=      !=      !=      !=      !=
     *     :baz   !=      ==      !=      !=      !=      !=
     *  foo:bar   !=      !=      ==      !=      !=      !=
     *  biz:baz   !=      !=      !=      ==      !=      !=
     *  qux:baz   !=      !=      !=      !=      ==      !=
     *    value   !=      !=      !=      !=      !=      ==
     * </pre>
     */
    static final boolean[][] EQUALITY_MATRIX = {
            {true, false, false, false, false, false,},
            {false, true, false, false, false, false,},
            {false, false, true, false, false, false,},
            {false, false, false, true, false, false,},
            {false, false, false, false, true, false,},
            {false, false, false, false, false, true,}
    };

    /**
     * Defined as: {@code [:bar, :baz, foo:bar, foo:baz, qux:baz, value]}
     */
    static final Object[] VECTOR = {
            BAR,
            BAZ,
            FOO_BAR,
            BIZ_BAZ,
            QUX_BAZ,
            VALUE
    };

    static Stream<EqualityEdge> getEqualityEdges() {
        return TestUtil.asEqualityEdges(EQUALITY_MATRIX, VECTOR);
    }

    private static Stream<Arguments> equalRefsSource() {
        return getEqualityEdges().filter(EqualityEdge::isEqual)
                                 .map(edge -> arguments(edge.getA(), edge.getB()));
    }

    private static Stream<Arguments> nonEqualRefsSource() {
        return getEqualityEdges().filter(not(EqualityEdge::isEqual))
                                 .map(edge -> arguments(edge.getA(), edge.getB()));
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
        assertThat(FOO_BAR.getValue()).isEqualTo(FOO_BAR.getModel().ref(FOO_BAR.getName()));
    }

    @Test
    void setValue() {
        assertThrows(UnsupportedOperationException.class, () -> FOO_BAR.setValue(BAZ));
    }
}
