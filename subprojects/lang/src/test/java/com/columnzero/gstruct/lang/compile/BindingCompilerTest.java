package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.ExampleSources;
import com.columnzero.gstruct.model.NameBindings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

class BindingCompilerTest {

    static Stream<File> examplesSource() throws IOException {
        return ExampleSources.walkExamples(".gs");
    }

    @ParameterizedTest
    @MethodSource("examplesSource")
    void examples(File file) throws IOException {
        Object expect = ExampleSources.getHeader(file)
                                      .orElseThrow()
                                      .getExpectedFromGroovyScript();

        NameBindings actual = BindingCompiler.compile(file);

        assertThat(actual).isEqualTo(expect);
    }
}
