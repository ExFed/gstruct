package com.columnzero.gstruct;

import com.columnzero.gstruct.util.Comparators;
import com.columnzero.gstruct.util.Path;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMultimap;
import io.vavr.collection.Multimap;
import io.vavr.collection.Stream;
import io.vavr.collection.TreeMultimap;
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

import static com.columnzero.gstruct.SourceFile.sourceFile;

/**
 * A collection of source files.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceTree {

    public static SourceTree.Root root(File rootDirectory) {
        return root(rootDirectory.toPath());
    }

    public static SourceTree.Root root(java.nio.file.Path rootDirectory) {
        return new Root(rootDirectory);
    }

    /**
     * Root of the source tree. Defines the path to the root namespace.
     */
    @NonNull Root root;

    @NonNull Stream<SourceFile> files;

    /**
     * Gets the namespaces formed by the source tree.
     */
    public Map<SourceFile, Path<String>> getNamespaces() {

        final Map<SourceFile, Path<String>> namespaces = new LinkedHashMap<>();
        for (SourceFile source : getFiles()) {
            namespaces.put(source, source.getNamespace());
        }
        return namespaces;
    }

    public Multimap<Path<String>, SourceFile> mapByNamespace() {

        Multimap<Path<String>, SourceFile> map =
                TreeMultimap.withSeq().empty(Comparators::lexicographic);
        for (SourceFile source : getFiles()) {
            map = map.put(source.getNamespace(), source);
        }
        return map;
    }

    /**
     * The root of a source tree.
     */
    @Value
    public static class Root {

        @NonNull java.nio.file.Path directory;

        private Root(java.nio.file.Path directory) {
            if (!Files.isDirectory(directory)) {
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
                    Stream.ofAll(Files.walk(directory))
                          .map(java.nio.file.Path::toFile)
                          .filter(File::isFile)
                          .filter(selector)
                          .map(file -> sourceFile(this, file));
            return new SourceTree(this, sourceFiles);
        }
    }
}
