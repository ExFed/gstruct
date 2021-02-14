package com.columnzero.gstruct;

import com.columnzero.gstruct.util.Path;
import io.vavr.collection.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A collection of source files.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceTree {

    public static SourceTree.Root root(File rootDirectory) {
        return new Root(rootDirectory);
    }

    /**
     * Root of the source tree. Defines the path to the root namespace.
     */
    @NonNull Root root;

    @NonNull Stream<SourceFile> files;

    /**
     * The root of the source tree. Determines the namespace of declarations.
     *
     * @return A directory.
     */
    public File getRootDirectory() {
        return root.directory;
    }

    /**
     * Gets the namespaces formed by the source tree.
     *
     * @return A namespace trie.
     */
    public Map<SourceFile, Path<String>> getNamespaces() {

        final Map<SourceFile, Path<String>> namespaces = new LinkedHashMap<>();
        for (SourceFile source : getFiles()) {
            final File rootDir = getRootDirectory();
            final var namespace = source.getNamespace(rootDir);
            namespaces.put(source, namespace);
        }
        return namespaces;
    }

    /**
     * The root of a source tree.
     */
    @Value
    public static class Root {

        @NonNull File directory;

        private Root(File directory) {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("Root is not a directory.");
            }
            this.directory = directory;
        }

        /**
         * Selects source files within the root directory.
         *
         * @param filenameExtension Extension of the source files.
         *
         * @return A new source tree.
         *
         * @throws IOException If an I/O error occurs while walking the directory tree.
         */
        public SourceTree select(String filenameExtension) throws IOException {
            final String suffix = "." + filenameExtension;
            return select(file -> file.getName().endsWith(suffix));
        }

        /**
         * Selects source files within the root directory.
         *
         * @param selector Predicate used to select source files. Is only passed regular files.
         *
         * @return A new source tree.
         *
         * @throws IOException If I/O error occurs while walking the directory tree.
         */
        public SourceTree select(Predicate<File> selector) throws IOException {
            final var sourceFiles =
                    Stream.ofAll(Files.walk(directory.toPath()))
                          .map(java.nio.file.Path::toFile)
                          .filter(File::isFile)
                          .filter(selector)
                          .map(SourceFile::new);
            return new SourceTree(this, sourceFiles);
        }
    }
}
