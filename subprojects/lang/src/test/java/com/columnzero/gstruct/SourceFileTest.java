package com.columnzero.gstruct;

import com.columnzero.gstruct.lang.Namespace;
import com.columnzero.gstruct.lang.TestFileUtil;
import com.columnzero.gstruct.util.Path;
import com.columnzero.gstruct.util.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SourceFileTest {

    @TempDir
    File tempDir;

    static final List<String> FILENAMES = List.of(
            "foobar.gsml",
            "foo/bar/biz.gsml"
    );

    @BeforeEach
    void setUp() {
        FILENAMES.stream()
                 .map(s -> new File(tempDir, s))
                 .forEach(TestFileUtil::makeFile);
    }

    private static Stream<Arguments> getNamespaceSource() {
        return FILENAMES.stream()
                        .map(s -> arguments(s, Namespace.from(s.split("/")).getParent()));
    }

    @ParameterizedTest
    @MethodSource("getNamespaceSource")
    void getNamespace(String filePath, Namespace expect) {

        final File file = new File(tempDir, filePath);
        final SourceFile sourceFile = new SourceFile(file);

        assertThat(sourceFile.getFile()).isEqualTo(file);
        assertThat(sourceFile.getNamespace(tempDir)).isEqualTo(expect);
    }
}
