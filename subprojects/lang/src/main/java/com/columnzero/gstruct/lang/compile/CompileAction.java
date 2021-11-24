package com.columnzero.gstruct.lang.compile;

import java.io.IOException;

@FunctionalInterface
public interface CompileAction {
    void execute() throws IOException;
}
