package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.ExampleSources;
import com.columnzero.gstruct.model.NameBindings;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BindingCompilerTest {

    static Stream<File> examplesSource() throws IOException {
        return Stream.ofAll(ExampleSources.walkExamples(".gs"));
    }

    static Stream<Arguments> examplesWithExpectedSource()
            throws IOException {
        return examplesSource().flatMap(
                file -> ExampleSources.getHeader(file)
                                      .flatMap(ExampleSources.Header::getExpectedFromGroovyScript)
                                      .map(expect -> arguments(file, expect)));
    }

    @ParameterizedTest
    @MethodSource("examplesWithExpectedSource")
    void examples(File file, Either<Throwable, Object> expectEither) throws IOException {
        if (expectEither.isLeft()) {
            Throwable expect = expectEither.getLeft();
            assertThrows(expect.getClass(), () -> BindingCompiler.compile(file));
        } else {
            Object expect = expectEither.get();
            NameBindings actual = BindingCompiler.compile(file);
            assertThat(actual).isEqualTo(expect);
        }
    }
}
