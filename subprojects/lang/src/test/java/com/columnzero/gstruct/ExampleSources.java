package com.columnzero.gstruct;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

public class ExampleSources {
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
}
