package com.columnzero.gstruct.lang.compile;

import java.io.IOException;

@FunctionalInterface
interface CompileAction {
    void execute() throws IOException;
}
