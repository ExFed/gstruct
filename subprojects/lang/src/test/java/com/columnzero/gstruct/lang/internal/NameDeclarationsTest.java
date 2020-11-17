package com.columnzero.gstruct.lang.internal;

import com.columnzero.gstruct.ExampleSources;
import com.columnzero.gstruct.lang.Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.columnzero.gstruct.lang.Parser.withSource;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class NameDeclarationsTest {

    @Test
    void parseSomePrimitives() {
        final String src = "" +
                "typedef String : primitive\n" +
                "typedef Number : primitive\n" +
                "typedef Bool : primitive\n";

        final Set<String> expect = Set.of("String", "Number", "Bool");

        final Parser<String> parser = withSource(src);
        final NameDeclarations scraper = new NameDeclarations();

        parser.run(scraper);

        assertThat(scraper.$names()).containsExactlyElementsIn(expect);
    }

    @Test
    void parseClosures() {
        final String src = "" +
                "struct A : primitive {}\n" +
                "struct B : A {}\n" +
                "struct C : {}\n" +
                "typedef X : primitive {}\n" +
                "typedef Y : X {}\n" +
                "typedef Z : {}\n";

        final Set<String> expect = Set.of("A", "B", "C", "X", "Y", "Z");

        final Parser<String> parser = withSource(src);
        final NameDeclarations scraper = new NameDeclarations();

        parser.run(scraper);

        assertThat(scraper.$names()).containsExactlyElementsIn(expect);
    }

    @Test
    void circular() {
        final String src = "" +
                "struct A : A\n";

        final Set<String> expect = Set.of("A");

        final Parser<String> parser = withSource(src);
        final NameDeclarations scraper = new NameDeclarations();

        parser.run(scraper);

        assertThat(scraper.$names()).containsExactlyElementsIn(expect);
    }

    static Stream<File> examplesSource() throws IOException {
        return ExampleSources.walkExamples();
    }

    @ParameterizedTest
    @MethodSource("examplesSource")
    void examples(File file) throws Exception {

        final Parser<File> parser = withSource(file);
        final NameDeclarations decls = parser.run(new NameDeclarations());
        final Optional<Set<String>> expectation =
                ExampleSources.getHeader(parser.getSource())
                              .map(ExampleSources.Header::getExpectedNames);

        assertWithMessage("Example file is missing expectations")
                .that(expectation.isPresent()).isTrue();
        assertThat(decls.$names()).containsExactlyElementsIn(expectation.get());
    }
}
