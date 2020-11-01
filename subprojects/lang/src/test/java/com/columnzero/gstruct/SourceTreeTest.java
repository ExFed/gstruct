package com.columnzero.gstruct;

import com.columnzero.gstruct.lang.TestFileUtil;
import com.columnzero.gstruct.util.Path;
import com.columnzero.gstruct.util.Trie;
import com.columnzero.gstruct.util.function.NoOp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.columnzero.gstruct.SourceFile.sourceFile;
import static com.google.common.truth.Truth.assertThat;

class SourceTreeTest {

    static final List<String> TEST_FILES = List.of(
            "foobar.gsml",
            "foo/bar/biz.gsml",
            "foo/bar/biz/baz.gsml",
            "foo.xyz",
            "bargsml"
    );

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        TEST_FILES.stream()
                  .map(f -> new File(tempDir, f))
                  .forEach(TestFileUtil::makeFile);
    }

    @Test
    void getNamespaces() throws IOException {
        final SourceTree.Root root = SourceTree.root(tempDir);
        final SourceTree gsmlTree = root.select("gsml");
        final Trie<String, SourceFile> expect =
                TEST_FILES.stream()
                          .filter(s -> s.endsWith(".gsml"))
                          .collect(Trie::new,
                                   (t, s) -> t.put(Path.of(s.split("/")).getParent(),
                                                   sourceFile(new File(tempDir, s))),
                                   NoOp::noopVoid);
        final Trie<String, SourceFile> actual = gsmlTree.getNamespaces();
        assertThat(actual).isEqualTo(expect);
    }
}
