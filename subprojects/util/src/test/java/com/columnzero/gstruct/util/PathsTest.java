package com.columnzero.gstruct.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathsTest {

    private List<String> nameList(java.nio.file.Path path) {
        return StreamSupport.stream(path.spliterator(), false)
                            .map(java.nio.file.Path::getFileName)
                            .map(java.nio.file.Path::toString)
                            .collect(Collectors.toList());
    }

    @Test
    void fromNioPath() {
        final java.nio.file.Path nioPathRel = java.nio.file.Paths.get("foo", "bar", "biz", "baz");
        final java.nio.file.Path nioPathAbs = nioPathRel.toAbsolutePath();

        final List<String> absNames = nameList(nioPathAbs);
        final List<String> relNames = nameList(nioPathRel);

        final Path<String> pathAbs = Paths.from(nioPathAbs);
        final Path<String> pathRel = Paths.from(nioPathRel);
        final Path<String> pathNull = Paths.from(null);
        final Path<String> pathDot = Paths.from(java.nio.file.Paths.get("."));
        final Path<String> pathDotSlash = Paths.from(java.nio.file.Paths.get("./"));
        final Path<String> pathEmpty = Paths.from(java.nio.file.Paths.get(""));

        assertAll(
                () -> assertThat(pathAbs).containsExactlyElementsIn(absNames).inOrder(),
                () -> assertThat(pathRel).containsExactlyElementsIn(relNames).inOrder(),
                () -> assertThat(pathNull).isEqualTo(Path.getRoot()),
                () -> assertThat(pathDot).isEqualTo(Path.getRoot()),
                () -> assertThat(pathDotSlash).isEqualTo(Path.getRoot()),
                () -> assertThat(pathEmpty).isEqualTo(Path.getRoot())
        );
    }

    @Test
    void fromFile() {

        final List<String> foobar = List.of("foo", "bar", "biz", "baz");
        final File cwd = new File(".");

        final Path<String> pathFoobar = Paths.from(new File(String.join("/", foobar)), cwd);
        final Path<String> pathEmpty = Paths.from(cwd, cwd);

        assertAll(
                () -> assertThat(pathFoobar).containsExactlyElementsIn(foobar).inOrder(),
                () -> assertThat(pathEmpty).isEqualTo(Path.getRoot())
        );
    }
}
