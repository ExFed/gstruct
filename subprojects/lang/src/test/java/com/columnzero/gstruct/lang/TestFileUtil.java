package com.columnzero.gstruct.lang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestFileUtil {

    /**
     * Makes an empty file and directories leading to it if they do not yet exist.
     *
     * @param file The file to make.
     *
     * @return The file.
     */
    public static File makeFile(File file) {
        var path = file.toPath();
        try {
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (Files.notExists(path)) {
                Files.createFile(path).toFile();
            }
            return path.toFile();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
