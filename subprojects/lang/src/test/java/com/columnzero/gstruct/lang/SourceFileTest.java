package com.columnzero.gstruct.lang;

import com.columnzero.gstruct.util.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SourceFileTest {

    static File makeFile(File file) {
        var path = file.toPath();
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            return (Files.notExists(path) ? Files.createFile(path) : path).toFile();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @TempDir
    File tempDir;

    List<File> testSources;

    @BeforeEach
    void setUp() {
        final Stream<String> filenames = Stream.of(
                "foobar.gsml",
                "foo/bar/biz.gsml"
        );
        testSources = filenames.map(s -> new File(tempDir, s))
                               .map(SourceFileTest::makeFile)
                               .collect(Collectors.toList());
    }

    private static Stream<Arguments> getNamespaceSource() {
        return Stream.of(
                arguments("foobar.gsml", Path.of()),
                arguments("foo/bar/biz.gsml", Path.of("foo", "bar"))
        );
    }

    @ParameterizedTest
    @MethodSource("getNamespaceSource")
    void getNamespace(String filePath, Path<String> expect) throws IOException {

        final File file = new File(tempDir, filePath);
        final SourceFile sourceFile = new SourceFile(file);

        assertThat(sourceFile.getFile()).isEqualTo(file);
        assertThat(sourceFile.getNamespace(tempDir)).isEqualTo(expect);
    }
}
