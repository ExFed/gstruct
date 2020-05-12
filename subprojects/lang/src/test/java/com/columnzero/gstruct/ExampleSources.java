package com.columnzero.gstruct;

import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class ExampleSources {

    public static final String LINE_COMMENT = "//";

    private ExampleSources() {
        throw new UnsupportedOperationException("utility");
    }

    /**
     * Gets the conventional path used for example code. Validates that it exists and is a
     * directory.
     *
     * @return A path pointing to a valid directory.
     */
    public static Path getExamplesDir() {
        final ClassLoader classLoader = ExampleSources.class.getClassLoader();
        final URL examplesUrl = requireNonNull(classLoader.getResource("exampleSrc"),
                                               "Could not get resource");
        final Path dir;
        try {
            dir = Paths.get(examplesUrl.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }

        if (!Files.isDirectory(dir)) {
            throw new IllegalStateException("Resource is not a directory");
        }

        return dir;
    }


    /**
     * Gets a YAML-formatted example header if it exists.
     *
     * @param exampleFile File to scan for a header.
     *
     * @return A YAML mapping, if it exists.
     *
     * @throws FileNotFoundException If the file does not exist.
     */
    public static Optional<Header> getHeader(File exampleFile) throws FileNotFoundException {
        final Scanner scanner = new Scanner(exampleFile);

        if (!scanner.hasNextLine()) {
            // doesn't have any text...
            return Optional.empty();
        }

        // detect block vs line comments
        final String firstLine = scanner.nextLine().stripLeading();
        final String headerText;
        switch (firstLine) {
            case "/*+++":
                // block comment style
                headerText = getBlockStyleHeader(scanner);
                break;
            case "//+++":
                // line comment style
                headerText = getLineStyleHeader(scanner);
                break;
            default:
                // incorrect format, ignore
                return Optional.empty();
        }

        return Optional.of(new Header(stripIndent(headerText)));
    }

    private static String getBlockStyleHeader(Scanner scanner) {
        final Stream.Builder<String> lines = Stream.builder();
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            // look for an end-of-comment
            final int blockEnd = line.indexOf("*/");
            if (blockEnd >= 0) {
                // include chars preceding the end-of-comment
                lines.add(line.substring(0, blockEnd));
                break;
            }
            lines.add(line);
        }
        return lines.build().collect(Collectors.joining("\n"));
    }

    private static String getLineStyleHeader(Scanner scanner) {
        final Stream.Builder<String> lines = Stream.builder();
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (!line.startsWith(LINE_COMMENT)) {
                break;
            }
            lines.add(line.substring(LINE_COMMENT.length()));
        }
        return lines.build().collect(Collectors.joining("\n"));
    }

    private static String stripIndent(String str) {
        return StringGroovyMethods.stripIndent(str);
    }

    /**
     * Represents a TOML-formatted example file header containing test metadata, such as
     * expectations, etc.
     */
    public static class Header {

        private final TomlParseResult toml;

        private Header(String text) {
            this.toml = Toml.parse(text);
        }

        public Set<String> getExpectedNames() {
            final TomlArray names = toml.getArray("expect.names");

            if (null == names || !names.containsStrings()) {
                return Collections.emptySet();
            }

            return names.toList()
                        .stream()
                        .map(String.class::cast)
                        .collect(Collectors.toSet());
        }
    }
}
