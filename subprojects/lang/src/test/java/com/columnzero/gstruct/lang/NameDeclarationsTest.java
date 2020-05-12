package com.columnzero.gstruct.lang;

import com.columnzero.gstruct.ExampleSources;
import com.columnzero.gstruct.TestSourceParser;
import com.columnzero.gstruct.util.function.CallResult;
import groovy.lang.Binding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.columnzero.gstruct.TestSourceParser.withSource;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NameDeclarationsTest {

    @Test
    void parseSomePrimitives() {
        final String src = "" +
                "typedef String : primitive\n" +
                "typedef Number : primitive\n" +
                "typedef Bool : primitive\n";

        final Set<String> expect = new HashSet<>(Arrays.asList("String", "Number", "Bool"));

        final TestSourceParser<String> parser = withSource(src);
        final Binding binding = parser.getBinding();
        final NameDeclarations scraper = new NameDeclarations(binding);

        parser.run(scraper);

        assertThat(scraper.$names()).containsExactlyElementsIn(expect);
        assertThat(binding.getVariables().keySet()).containsAtLeastElementsIn(expect);
    }

    @Test
    void examples() throws Exception {
        final var sources = Files.walk(ExampleSources.getExamplesDir())
                                 .map(Path::toFile)
                                 .filter(File::isFile)
                                 .filter(f -> f.getName().endsWith(".gsml"))
                                 .collect(Collectors.toSet());
        final var parserRes = sources.stream()
                                     .map(file -> CallResult.of(() -> withSource(file)))
                                     .collect(Collectors.partitioningBy(CallResult::isSuccess));

        // make sure we don't have any errors
        assertThat(parserRes.get(false)).isEmpty();

        final List<Executable> execs = new ArrayList<>();
        for (CallResult<TestSourceParser<File>> result : parserRes.get(true)) {
            final TestSourceParser<File> parser = result.getValue();
            final NameDeclarations decls = parser.run(NameDeclarations::new);
            final Set<String> expect = ExampleSources.getHeader(parser.getSource())
                                                     .map(ExampleSources.Header::getExpectedNames)
                                                     .orElseThrow(() -> new AssertionError(
                                                             "Missing example header"));

            execs.add(() -> assertThat(decls.$names()).isEqualTo(expect));
        }
        assertAll(execs);
    }
}
