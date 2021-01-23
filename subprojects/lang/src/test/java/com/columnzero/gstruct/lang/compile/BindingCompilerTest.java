package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.ExampleSources;
import com.columnzero.gstruct.ExampleSources.Header;
import com.columnzero.gstruct.model.NameBindings;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static io.vavr.CheckedFunction1.liftTry;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BindingCompilerTest {

    static Stream<File> examplesSource() throws IOException {
        return Stream.ofAll(ExampleSources.walkExamples(".gs"));
    }

    static Stream<Arguments> examplesWithExpectedSource() throws IOException {
        return examplesSource().flatMap(
                file -> ExampleSources.getHeader(file)
                                      .map(liftTry(Header::getExpectedFromGroovyScript))
                                      // sneaky-throw the IOException
                                      .flatMap(Try::get)
                                      .map(pair -> arguments(file, pair)));
    }

    @ParameterizedTest
    @MethodSource("examplesWithExpectedSource")
    void examples(File file, Either<Class<Throwable>, Object> expectEither) throws IOException {
        if (expectEither.isLeft()) {
            Class<Throwable> expect = expectEither.getLeft();
            assertThrows(expect, () -> BindingCompiler.compile(file));
        } else {
            Object expect = expectEither.get();
            NameBindings actual = BindingCompiler.compile(file);
            assertThat(actual).isEqualTo(expect);
        }
    }
}
