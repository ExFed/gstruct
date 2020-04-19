package com.columnzero.gstruct.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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

class PathTest {

    private final List<String> abc = asList("a", "b", "c");

    private Path<String> cut;

    @Test
    void properties() {
        cut = Path.of(abc);

        assertAll(
                () -> assertThat(cut.getDepth()).isEqualTo(abc.size()),
                () -> assertThat(cut.getValue()).isEqualTo("c"),
                () -> assertThat(cut.getParent().getValue()).isEqualTo("b")
        );
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void construction() {
        final Stream<Executable> pathOfCases = Stream.of(
                asList("a/b/c".split("/")),
                asList("x/x/x".split("/")),
                singletonList("a"),
                List.<String>of()
        ).map(pl -> () -> assertThat(Path.of(pl).asList()).isEqualTo(pl));

        final List<String> nullStrings = null;

        final Path<String> xPath = Path.of("x");
        final Stream<Executable> errorCases = Stream.of(
                () -> assertThrows(NullPointerException.class, () -> Path.of(nullStrings)),
                () -> assertThrows(NullPointerException.class, () -> xPath.child((String) null)),
                () -> assertThrows(NullPointerException.class, () -> xPath.child(nullStrings))
        );

        final Stream<Executable> cases = Stream.of(pathOfCases, errorCases)
                                               .reduce(Stream::concat)
                                               .get();
        assertAll(cases);
    }

    @Test
    void pathToString() {
        assertThat(Path.of(abc).toString()).isEqualTo("/" + String.join("/", abc));
    }

    @Test
    void iterator() {
        cut = Path.of(abc);

        final Iterator<String> abcIter = abc.iterator();
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
        cut = Path.of(abc);

        final List<String> actual = StreamSupport.stream(cut.spliterator(), false)
                                                 .collect(Collectors.toList());

        assertThat(actual).isEqualTo(abc);
    }

    @Test
    void equalsAndHashCode() {
        final Path<String> abc1 = Path.of(abc);
        final Path<String> abc2 = Path.of(abc);
        final Path<String> xyz = Path.of("xyz".split(""));
        final Path<String> xyzabc = Path.of("xyzabc".split(""));
        final Path<String> ccc = Path.of("ccc".split(""));

        assertEqualsAndHashCode(abc1, abc2, xyz, xyzabc, ccc);
    }
}
