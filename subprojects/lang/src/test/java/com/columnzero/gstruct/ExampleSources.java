package com.columnzero.gstruct;

import com.columnzero.gstruct.lang.compile.DelegatingGroovyParser;
import groovy.lang.Closure;
import groovy.util.DelegatingScript;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Value;
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
        final URL examplesUrl = requireNonNull(classLoader.getResource("examples"),
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
     * Walks all source files in the examples directory with the given file suffix.
     *
     * @param suffix Filename suffix to filter on (e.g. ".gs")
     *
     * @return A stream of files.
     */
    public static Stream<File> walkExamples(String suffix) {
        final var dir = ExampleSources.getExamplesDir();
        try {
            return Stream.ofAll(Files.walk(dir))
                         .map(Path::toFile)
                         .filter(File::isFile)
                         .filter(f -> f.getName().endsWith(suffix));
        } catch (IOException e) {
            throw new Error("Could not get examples with suffix '" + suffix + "' in " + dir, e);
        }
    }

    public static SourceTree getExamplesTree(String extension) {
        final var dir = getExamplesDir();
        try {
            return SourceTree.root(dir).select(extension);
        } catch (IOException e) {
            throw new Error(
                    "Could not get examples with extension '" + extension + "' in " + dir, e);
        }
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

            final TomlParseResult tomlHeader = Toml.parse(stripIndent(headerText));
            return Option.some(new Header(exampleFile, tomlHeader));
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
    @Value
    public static class Header {

        File sourceFile;
        TomlParseResult toml;

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
        public Option<Either<Class<? extends Throwable>, Object>> getExpectedFromGroovyScript() {
            var parser = new DelegatingGroovyParser();
            var sourceDir = sourceFile.getParentFile();
            var scriptFileOpt =
                    Option.of(toml.getString("expect.script.groovy.file"))
                          .map(filename -> new File(sourceDir, filename));

            DelegatingScript script;
            if (scriptFileOpt.isEmpty()) {
                var scriptSrcOpt =
                        Option.of(toml.getString("expect.script.groovy.source"));
                if (scriptSrcOpt.isEmpty()) {
                    return Option.none();
                }
                script = parser.parse(scriptSrcOpt.get());
            } else {
                final var scriptFile = scriptFileOpt.get();
                try {
                    script = parser.parse(scriptFile);
                } catch (IOException e) {
                    throw new Error("Could not get result from script (" + scriptFile + ")", e);
                }
            }

            var errorClosure = new Closure<Void>(parser.getShell()) {
                Class<? extends Throwable> clazz = null;

                @SuppressWarnings("unused")
                public void doCall(Class<? extends Throwable> exceptionClass) {
                    clazz = exceptionClass;
                }
            };

            script.getBinding().setVariable("error", errorClosure);

            var result = script.run();
            return Option.some(null != errorClosure.clazz
                                       ? Either.left(errorClosure.clazz)
                                       : Either.right(result));
        }
    }
}
