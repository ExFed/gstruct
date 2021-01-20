package com.columnzero.gstruct;

import com.columnzero.gstruct.lang.compile.DelegatingGroovyParser;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.tinylog.Logger;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Walks all source files in the examples directory with the given file extension.
     *
     * @param extension Filename extension to filter on (e.g. ".gsml")
     *
     * @return A stream of files.
     *
     * @throws IOException If there is error while walking the file tree.
     */
    public static Stream<File> walkExamples(String extension) throws IOException {
        return Stream.ofAll(Files.walk(ExampleSources.getExamplesDir()))
                     .map(Path::toFile)
                     .filter(File::isFile)
                     .filter(f -> f.getName().endsWith(extension));
    }

    /**
     * Gets a header if it exists on the given file.
     *
     * @param exampleFile File to scan for a header.
     *
     * @return a header, if the file exists and has a header
     */
    public static Option<Header> getHeader(File exampleFile) {
        try (Scanner scanner = new Scanner(exampleFile)) {
            final String headerText;
            if (!scanner.hasNextLine()) {
                // doesn't have any text...
                return Option.none();
            }

            // detect block vs line comments
            final String firstLine = scanner.nextLine().stripLeading();
            switch (firstLine) {
                case "/*+++":
                    // block comment style
                    headerText = readBlockStyleHeader(scanner);
                    break;
                case "//+++":
                    // line comment style
                    headerText = readLineStyleHeader(scanner);
                    break;
                default:
                    // incorrect format, ignore
                    return Option.none();
            }

            return Option.some(new Header(stripIndent(headerText)));
        } catch (FileNotFoundException e) {
            Logger.error(e);
            return Option.none();
        }
    }

    private static String readBlockStyleHeader(Scanner scanner) {
        Stream<String> lines = Stream.of();
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            // look for an end-of-comment
            final int blockEnd = line.indexOf("*/");
            if (blockEnd >= 0) {
                // include chars preceding the end-of-comment
                lines = lines.append(line.substring(0, blockEnd));
                break;
            }
            lines = lines.append(line);
        }
        return lines.collect(Collectors.joining("\n"));
    }

    private static String readLineStyleHeader(Scanner scanner) {
        Stream<String> lines = Stream.of();
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (!line.startsWith(LINE_COMMENT)) {
                break;
            }
            lines = lines.append(line.substring(LINE_COMMENT.length()));
        }
        return lines.collect(Collectors.joining("\n"));
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

        /**
         * Defined by the key {@code expect.names}.
         *
         * @return a set of expected names
         */
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

        /**
         * Executes a script defined by key {@code expect.script.groovy} and returns the result.
         *
         * @return if a script block is defined, either the thrown or returned value
         */
        public Option<Either<Throwable, Object>> getExpectedFromGroovyScript() {
            var parser = new DelegatingGroovyParser();
            return Option.of(toml.getString("expect.script.groovy"))
                         .map(parser::parse)
                         .map(script -> Try.of(script::run))
                         .map(Try::toEither);
        }
    }
}
