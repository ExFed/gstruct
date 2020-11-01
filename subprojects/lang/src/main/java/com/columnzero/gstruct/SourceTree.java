package com.columnzero.gstruct;

import com.columnzero.gstruct.util.Trie;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A collection of source files.
 */
public class SourceTree {

    public static SourceTree.Root root(File rootDirectory) {
        return new Root(rootDirectory);
    }

    private final @NonNull Root root;
    private final @NonNull Collection<SourceFile> sourceFiles;

    private SourceTree(Root root, Collection<SourceFile> sourceFiles) {
        this.root = root;
        this.sourceFiles = sourceFiles;
    }

    /**
     * Gets source files contained within the source tree.
     *
     * @return A collection of source files.
     *
     */
    public Collection<SourceFile> getFiles() {
        return sourceFiles;
    }

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
    public Trie<String, SourceFile> getNamespaces() {
        final Trie<String, SourceFile> namespaces = new Trie<>();
        for (SourceFile source : getFiles()) {
            final File rootDir = getRootDirectory();
            final var namespace = source.getNamespace(rootDir);
            namespaces.put(namespace, source);
        }
        return namespaces;
    }

    /**
     * The root of a source tree.
     */
    public static final class Root {

        private final File directory;

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
            final Set<SourceFile> sourceFiles = Files.walk(directory.toPath())
                                                     .map(Path::toFile)
                                                     .filter(File::isFile)
                                                     .filter(selector)
                                                     .map(SourceFile::new)
                                                     .collect(Collectors.toSet());
            return new SourceTree(this, sourceFiles);
        }
    }
}
