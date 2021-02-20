package com.columnzero.gstruct.util;

import lombok.Value;
import org.junit.jupiter.api.function.Executable;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TestUtil {
    private TestUtil() {
        throw new UnsupportedOperationException("utility");
    }

    @SafeVarargs
    @SuppressWarnings({"ConstantConditions", "EqualsWithItself"})
    public static <T> void assertEqualsAndHashCode(T a1, T a2, T b, T... etc) {
        final Stream.Builder<Executable> exec = Stream.builder();

        // null
        exec.add(() -> assertThat(a1.equals(null)).isFalse())
            .add(() -> assertThat(a2.equals(null)).isFalse())
            .add(() -> assertThat(b.equals(null)).isFalse());

        // type
        exec.add(() -> assertThat(a1.equals(new Object() {
        })).isFalse());

        // reflexive
        exec.add(() -> assertThat(a1.equals(a1)).isTrue());

        // symmetric
        exec.add(() -> assertThat(a1.equals(a2)).isTrue())
            .add(() -> assertThat(a2.equals(a1)).isTrue())
            .add(() -> assertThat(b.equals(a1)).isFalse())
            .add(() -> assertThat(a1.equals(b)).isFalse());

        // consistent
        for (int i = 0; i < 10; i++) {
            exec.add(() -> assertThat(a1.equals(a2)).isTrue())
                .add(() -> assertThat(a1.equals(b)).isFalse());
        }
        for (T e : etc) {
            exec.add(() -> assertThat(a1.equals(e)).isFalse())
                .add(() -> assertThat(e.equals(a1)).isFalse());
        }

        // hashcode
        final int a1Code = a1.hashCode();
        final int a2Code = a2.hashCode();
        exec.add(() -> assertThat(a1Code).isEqualTo(a2Code))
            .add(() -> assertThat(a1.hashCode()).isEqualTo(a1Code))
            .add(() -> assertThat(b.hashCode()).isNotEqualTo(a1Code));

        assertAll(exec.build());
    }

    public static Stream<EqualityEdge> asEqualityEdges(boolean[][] eqMatrix, Object[] testVector) {
        for (boolean[] row : eqMatrix) {
            assert row.length == eqMatrix.length : "matrix must be square";
        }
        assert eqMatrix.length == testVector.length
                : "vector dimension must equal matrix dimension";

        Stream.Builder<EqualityEdge> edges = Stream.builder();
        for (int rowIdx = 0; rowIdx < eqMatrix.length; rowIdx++) {
            boolean[] equalityRow = eqMatrix[rowIdx];
            for (int colIdx = 0; colIdx < equalityRow.length; colIdx++) {
                var isEqual = equalityRow[colIdx];
                var row = testVector[rowIdx];
                var col = testVector[colIdx];
                edges.add(new EqualityEdge(row, col, isEqual));
            }
        }
        return edges.build();
    }

    @Value
    public static class EqualityEdge {
        Object a;
        Object b;
        boolean equal;
    }
}
