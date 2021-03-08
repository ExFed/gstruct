package com.columnzero.gstruct.codegen;

import com.columnzero.gstruct.ExampleSources;
import com.columnzero.gstruct.lang.compile.NominalCompiler;
import com.columnzero.gstruct.model.Identifier;
import com.columnzero.gstruct.model.Identifier.Name;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static io.vavr.control.Either.right;

class SimpleGroovyCodegenTest {

    @Test
    void integration() throws IOException, ClassNotFoundException {
        final var latLonSrc =
                ExampleSources.walkExamples(".gs")
                              .find(file -> "latlon.gs".equals(file.getName()))
                              .get();
        final var expectContentFile =
                ExampleSources.walkExamples(".expect.c")
                              .find(file -> "latlon.kinda_like_c.expect.c".equals(file.getName()))
                              .get();
        final var expectContent = Files.readString(expectContentFile.toPath());
        final var templateSrc =
                ExampleSources.walkExamples(".gst")
                              .find(file -> "kinda_like_c.gst".equals(file.getName()))
                              .get();

        final var model = NominalCompiler.compile(latLonSrc, Identifier.name());
        final var codegen = new SimpleGroovyCodegen();
        final Map<Name, StringWriter> writers = new TreeMap<>();
        codegen.run(model,
                    new FileReader(templateSrc),
                    name -> right(writers.computeIfAbsent(name, n -> new StringWriter())));

        final var actualContent = writers.values()
                                   .stream()
                                   .map(StringWriter::toString)
                                   .collect(Collectors.joining("\n"));

        assertThat(actualContent).isEqualTo(expectContent);
        assertThat(writers.keySet()).containsExactlyElementsIn(model.getBindings().keySet());
    }
}
