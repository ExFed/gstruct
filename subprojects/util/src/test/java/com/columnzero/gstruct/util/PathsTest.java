package com.columnzero.gstruct.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Iterator;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathsTest {

    private void validatePathFromNio(java.nio.file.Path nioPath) {
        final Path<String> path = Paths.from(nioPath);

        final Iterator<java.nio.file.Path> nioIt = nioPath.iterator();
        final Iterator<String> pathIt = path.iterator();

        final Stream.Builder<Executable> exec = Stream.builder();
        while (nioIt.hasNext() && pathIt.hasNext()) {
            final String pathElem = pathIt.next();
            final String filename = nioIt.next().getFileName().toString();
            exec.add(() -> assertThat(pathElem).isEqualTo(filename));
        }

        exec.add(() -> assertThat(nioIt.hasNext()).isFalse())
            .add(() -> assertThat(pathIt.hasNext()).isFalse());

        assertAll(exec.build());
    }

    @Test()
    void from() {
        final java.nio.file.Path nioPathRel = java.nio.file.Paths.get("foo", "bar", "biz", "baz");
        final java.nio.file.Path nioPathAbs = nioPathRel.toAbsolutePath();

        validatePathFromNio(nioPathAbs);
        validatePathFromNio(nioPathRel);
    }
}
