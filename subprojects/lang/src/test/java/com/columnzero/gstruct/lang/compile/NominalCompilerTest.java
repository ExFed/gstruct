package com.columnzero.gstruct.lang.compile;

import com.columnzero.gstruct.ExampleSources.Header;
import com.columnzero.gstruct.SourceFile;
import com.columnzero.gstruct.model.Identifier.Name;
import com.columnzero.gstruct.model.NominalModel;
import io.vavr.Tuple;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static com.columnzero.gstruct.ExampleSources.getExamplesTree;
import static com.columnzero.gstruct.ExampleSources.getHeader;
import static com.columnzero.gstruct.model.Identifier.name;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class NominalCompilerTest {

    static Stream<Arguments> examplesTreeWithExpectedSource() {
        return getExamplesTree("gs")
                .mapByNamespace()
                .toStream()
                .flatMap(entry -> {
                    var ns = name(entry._1);
                    var src = entry._2;
                    return getHeader(src.getFile())
                            .flatMap(Header::getExpectedFromGroovyScript)
                            .map(expect -> Tuple.of(src, ns, expect));
                })
                .map(args -> arguments(args._1, args._2, args._3));
    }

    @ParameterizedTest
    @MethodSource("examplesTreeWithExpectedSource")
    void examplesAtRootNamespace(SourceFile src, Name namespace, Either<Class<Throwable>, Object> expectEither)
            throws IOException {
        if (expectEither.isLeft()) {
            Class<Throwable> expect = expectEither.getLeft();
            assertThrows(expect, () -> NominalCompiler.compile(src.getFile(), namespace));
        } else {
            Object expect = expectEither.get();
            NominalModel actual = NominalCompiler.compile(src.getFile(), namespace);
            assertThat(actual).isEqualTo(expect);
        }
    }
}
