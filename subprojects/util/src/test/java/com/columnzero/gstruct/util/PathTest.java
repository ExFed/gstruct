package com.columnzero.gstruct.util;

import io.vavr.control.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.columnzero.gstruct.util.TestUtil.assertEqualsAndHashCode;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PathTest {

    private static final List<String> ABC = List.of("a", "b", "c");

    private Path<String> cut;

    @BeforeEach
    void setUp() {
        cut = Path.of(ABC);
    }

    @Test
    void properties() {
        cut = Path.of(ABC);

        assertAll(
                () -> assertThat(cut.getDepth()).isEqualTo(ABC.size()),
                () -> assertThat(cut.getValue()).isEqualTo("c"),
                () -> assertThat(cut.getParent().getValue()).isEqualTo("b")
        );
    }

    @SuppressWarnings("ConstantConditions")
    static Stream<Arguments> constructionSource() {
        final Stream<Arguments> happyCases = Stream.of(
                arguments(asList("a/b/c".split("/")), null),
                arguments(asList("x/x/x".split("/")), null),
                arguments(singletonList("a"), null),
                arguments(List.<String>of(), null)
        );

        final List<String> nullStrings = null;

        final Stream<Arguments> errorCases = Stream.of(
                arguments(nullStrings, NullPointerException.class)
        );

        return Stream.concat(happyCases, errorCases);
    }

    @ParameterizedTest
    @MethodSource("constructionSource")
    void construction(List<String> pathList, Class<? extends Throwable> exceptionType) {
        if (exceptionType == null) {
            assertThat(Path.of(pathList)).containsExactlyElementsIn(pathList).inOrder();
            assertThat(Path.path(pathList.toArray())).containsExactlyElementsIn(pathList).inOrder();
        } else {
            assertThrows(exceptionType, () -> Path.of(pathList));
            assertThrows(exceptionType, () -> Path.path(pathList.toArray()));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void child() {
        final List<String> abcd = asList("a/b/c/d".split("/"));
        final List<String> abcabc = Stream.concat(ABC.stream(), ABC.stream())
                                          .collect(Collectors.toList());

        assertThat(cut.child("d")).containsExactlyElementsIn(abcd).inOrder();
        assertThat(cut.child(cut)).containsExactlyElementsIn(abcabc).inOrder();

        assertThrows(NullPointerException.class, () -> cut.child((String) null));
        assertThrows(NullPointerException.class, () -> cut.child((Iterable<String>) null));
    }

    @Test
    void pathToString() {
        assertThat(Path.of(ABC).toString()).isEqualTo("/" + String.join("/", ABC));
    }

    @Test
    void iterator() {
        cut = Path.of(ABC);

        final Iterator<String> abcIter = ABC.iterator();
        final Iterator<String> cutIter = cut.iterator();

        final Stream.Builder<Executable> exec = Stream.builder();
        while (abcIter.hasNext() && cutIter.hasNext()) {
            final String expect = abcIter.next();
            final String actual = cutIter.next();

            exec.add(() -> assertThat(actual).isEqualTo(expect));
        }

        exec.add(() -> assertThat(abcIter.hasNext()).isFalse())
            .add(() -> assertThat(cutIter.hasNext()).isFalse());

        assertAll(exec.build());
    }

    @Test
    void spliterator() {
        cut = Path.of(ABC);

        final List<String> actual = StreamSupport.stream(cut.spliterator(), false)
                                                 .collect(Collectors.toList());

        assertThat(actual).isEqualTo(ABC);
    }

    @Test
    void equalsAndHashCode() {
        final Path<String> abc1 = Path.of(ABC);
        final Path<String> abc2 = Path.of(ABC);
        final Path<String> xyz = Path.of("xyz".split(""));
        final Path<String> xyzabc = Path.of("xyzabc".split(""));
        final Path<String> ccc = Path.of("ccc".split(""));

        assertEqualsAndHashCode(abc1, abc2, xyz, xyzabc, ccc);
    }

    @Test
    void foldLeft() {
        cut = Path.of(ABC);

        final var expect = "!abc";
        final var actual = cut.foldLeft("!", (s, s2) -> s + s2);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void foldRight() {
        cut = Path.of(ABC);

        final var expect = "abc!";
        final var actual = cut.foldRight("!", (s, s2) -> s + s2);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void reduceLeft() {
        cut = Path.of(ABC);

        final var expect = "abc";
        final var actual = cut.reduceLeft((s, s2) -> s + s2);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void reduceLeftOption() {
        cut = Path.of(ABC);

        final var expect = Option.of("abc");
        final var actual = cut.reduceLeftOption((s, s2) -> s + s2);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void reduceRight() {
        cut = Path.of(ABC);

        final var expect = "abc";
        final var actual = cut.reduceRight((s, s2) -> s + s2);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void reduceRightOption() {
        cut = Path.of(ABC);

        final var expect = Option.of("abc");
        final var actual = cut.reduceRightOption((s, s2) -> s + s2);

        assertThat(actual).isEqualTo(expect);
    }
}
